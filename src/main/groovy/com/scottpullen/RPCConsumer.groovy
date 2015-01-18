package com.scottpullen

public class RPCConsumer {

    public String handleMessage(String message) {
        println("HERE IN RPCConsumer")
        println(message)

        message.toUpperCase()
    }
}
