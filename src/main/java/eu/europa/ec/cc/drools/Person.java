package eu.europa.ec.cc.drools;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class Person implements Serializable {

    private String id;
    private String name;
    private int age;

}
