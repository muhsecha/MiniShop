package com.pos.minishop.model;

public class ProductCategoryModel {
    String id, namaCategory;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getNamaCategory() {
        return namaCategory;
    }

    public void setNamaCategory(String namaCategory) {
        this.namaCategory = namaCategory;
    }

    @Override
    public String toString() {
        return namaCategory;
    }
}
