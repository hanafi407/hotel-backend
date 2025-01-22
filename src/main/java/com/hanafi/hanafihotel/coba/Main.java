package com.hanafi.hanafihotel.coba;

import com.hanafi.hanafihotel.coba.myObj.House;
import com.hanafi.hanafihotel.coba.myObj.Person;

import java.util.List;

public class Main {
    private final String name = "Hanafi";
    public static void main(String[] args) {
        House house = House.builder().setFoundation("Batu").setRoof("Genteng").setStructure("Gedung").build();

        Person hanafi= new Person("Hanafi",31);
        Person dimas= new Person("Dimas",30);

        List<Person> persons =List.of(hanafi,dimas);

        System.out.println(persons.contains(new Person("Hanafi",33)));
    }
    public String getName(){
        return this.name;
    }
}
