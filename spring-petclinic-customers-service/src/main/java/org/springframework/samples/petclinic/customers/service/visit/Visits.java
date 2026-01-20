package org.springframework.samples.petclinic.customers.service.visit;

import java.util.ArrayList;
import java.util.List;

public class Visits {
    private List<VisitDetails> items = new ArrayList<>();

    public Visits() {
    }

    public Visits(List<VisitDetails> items) {
        this.items = items;
    }

    public List<VisitDetails> getItems() {
        return items;
    }

    public void setItems(List<VisitDetails> items) {
        this.items = items;
    }
}
