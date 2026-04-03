package com.example.the_cheaper.domain.model;

import lombok.*;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OptionAttribute {
    private Long id;
    private String name;
    private List<OptionValue> values;

    public void addValue(OptionValue value) {
        if (values == null)
            values = new ArrayList<>();
        values.add(value);
    }
}
