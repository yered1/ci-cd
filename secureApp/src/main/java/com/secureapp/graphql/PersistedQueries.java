package com.secureapp.graphql;
import java.util.Map;
public class PersistedQueries {
    public static final Map<String,String> QUERIES = Map.of(
        "q1", "{ myOrders { id itemName quantity status } }",
        "q2", "query($id: ID!){ order(id: $id) { id itemName quantity status } }"
    );
}
