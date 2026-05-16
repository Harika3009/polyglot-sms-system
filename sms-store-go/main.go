package main

import (
    "context"
    "encoding/json"
    "fmt"
    "log"
    "net/http"
    "strings"
    "time"

    "github.com/segmentio/kafka-go"
    "go.mongodb.org/mongo-driver/bson"
    "go.mongodb.org/mongo-driver/mongo"
    "go.mongodb.org/mongo-driver/mongo/options"
)

type SMS struct {
    UserID string `bson:"userId" json:"userId"`
    PhoneNumber string `bson:"phoneNumber" json:"phoneNumber"`
    Message string `bson:"message" json:"message"`
    Status string `bson:"status" json:"status"`
    CreatedAt time.Time `bson:"createdAt" json:"createdAt"`
}

var collection *mongo.Collection

func main() {
    client, err := mongo.Connect(context.TODO(), options.Client().ApplyURI("mongodb://localhost:27017"))
    if err != nil {
        log.Fatal(err)
    }

    collection = client.Database("smsdb").Collection("messages")

    go consumeKafka()

    http.HandleFunc("/v1/user/", getUserMessages)

    fmt.Println("Go Server running on port 8081...")
    log.Fatal(http.ListenAndServe(":8081", nil))
}

func consumeKafka() {
    reader := kafka.NewReader(kafka.ReaderConfig{
        Brokers: []string{"localhost:9092"},
        Topic: "sms-topic",
        GroupID: "sms-group",
    })

    for {
        msg, err := reader.ReadMessage(context.Background())
        if err != nil {
            log.Printf("kafka read error: %v", err)
            continue
        }

        var data SMS
        if err := json.Unmarshal(msg.Value, &data); err != nil {
            log.Printf("invalid kafka payload: %v", err)
            continue
        }

        data.CreatedAt = time.Now()

        _, err = collection.InsertOne(context.TODO(), data)
        if err != nil {
            log.Printf("mongo insert error: %v", err)
            continue
        }

        log.Printf("stored sms event for user %s", data.UserID)
    }
}

func getUserMessages(w http.ResponseWriter, r *http.Request) {
    parts := strings.Split(r.URL.Path, "/")
    if len(parts) < 5 {
        http.Error(w, "invalid url", http.StatusBadRequest)
        return
    }

    userId := parts[3]

    filter := bson.M{"userId": userId}
    cursor, err := collection.Find(context.TODO(), filter)
    if err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }

    results := []SMS{}
    if err = cursor.All(context.TODO(), &results); err != nil {
        http.Error(w, err.Error(), http.StatusInternalServerError)
        return
    }

    w.Header().Set("Content-Type", "application/json")
    json.NewEncoder(w).Encode(results)
}
