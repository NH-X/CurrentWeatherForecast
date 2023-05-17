package com.example.currentweatherforcecast.respost;

public class Resource<T> {
    private final T data;
    private final RequestProcessType type;
    private final String message;

    public Resource(T data,RequestProcessType type, String message) {
        this.data = data;
        this.type=type;
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public RequestProcessType getType(){
        return type;
    }

    public String getMessage() {
        return message;
    }
}