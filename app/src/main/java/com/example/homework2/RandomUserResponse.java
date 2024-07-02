package com.example.homework2;

import com.google.gson.annotations.SerializedName;

import java.util.List;

public class RandomUserResponse {
    private List<Result> results;

    public List<Result> getResults() {
        return results;
    }

    public static class Result {
        private Id id;
        private Name name;
        private String email;
        private Dob dob;
        private Location location;
        private Picture picture;

        public Id getId() { return id; }
        public Name getName() { return name; }
        public String getEmail() { return email; }
        public Dob getDob() { return dob; }
        public Location getLocation() { return location; }
        public Picture getPicture() { return picture; }

        public static class Id {
            private String value;
            public String getValue() { return value; }
        }

        public static class Name {
            private String first;
            private String last;
            public String getFirst() { return first; }
            public String getLast() { return last; }
        }

        public static class Dob {
            private int age;
            public int getAge() { return age; }
        }

        public static class Location {
            private String city;
            private String country;
            public String getCity() { return city; }
            public String getCountry() { return country; }
        }

        public static class Picture {
            private String large;
            public String getLarge() { return large; }
        }
    }
}