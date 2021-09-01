package com.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class User01 implements Serializable {
    private String name;
    private String age;
    private String text;
}
