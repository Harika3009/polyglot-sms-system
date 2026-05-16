package main

import "testing"

func TestSMSStruct(t *testing.T) {
    sms := SMS{UserID: "u1", Message: "hello"}
    if sms.UserID != "u1" {
        t.Fatal("expected valid user id")
    }
}
