package ru.gb.storage.commons;


import com.fasterxml.jackson.annotation.JsonTypeInfo;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.MINIMAL_CLASS,
        property = "type"
)
public class Message {
}
