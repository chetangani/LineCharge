package com.transvision.linecharge.values;

import java.io.Serializable;

public class GetSetValues implements Serializable {
    String linemen_id, linemen_name, subdiv_code;

    public GetSetValues() {
    }

    public String getLinemen_id() {
        return linemen_id;
    }

    public void setLinemen_id(String linemen_id) {
        this.linemen_id = linemen_id;
    }

    public String getLinemen_name() {
        return linemen_name;
    }

    public void setLinemen_name(String linemen_name) {
        this.linemen_name = linemen_name;
    }

    public String getSubdiv_code() {
        return subdiv_code;
    }

    public void setSubdiv_code(String subdiv_code) {
        this.subdiv_code = subdiv_code;
    }
}
