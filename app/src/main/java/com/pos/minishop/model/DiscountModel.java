package com.pos.minishop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class DiscountModel implements Parcelable {
    public static final Creator<DiscountModel> CREATOR = new Creator<DiscountModel>() {
        @Override
        public DiscountModel createFromParcel(Parcel in) {
            return new DiscountModel(in);
        }

        @Override
        public DiscountModel[] newArray(int size) {
            return new DiscountModel[size];
        }
    };
    private String id, name, discount;

    public DiscountModel() {

    }

    protected DiscountModel(Parcel in) {
        id = in.readString();
        name = in.readString();
        discount = in.readString();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDiscount() {
        return discount;
    }

    public void setDiscount(String discount) {
        this.discount = discount;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeString(discount);
    }

    @Override
    public String toString() {
        return name;
    }
}
