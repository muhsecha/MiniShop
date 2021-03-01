package com.pos.minishop.model;

import android.os.Parcel;
import android.os.Parcelable;

public class HistoryModel implements Parcelable {
    private String totalBuy, time, date, name, quantity, totalItem, price, trxNumber, image;

    public HistoryModel() {

    }

    protected HistoryModel(Parcel in) {
        totalBuy = in.readString();
        time = in.readString();
        date = in.readString();
        name = in.readString();
        quantity = in.readString();
        totalItem = in.readString();
        price = in.readString();
        trxNumber = in.readString();
        image = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(totalBuy);
        dest.writeString(time);
        dest.writeString(date);
        dest.writeString(name);
        dest.writeString(quantity);
        dest.writeString(totalItem);
        dest.writeString(price);
        dest.writeString(trxNumber);
        dest.writeString(image);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<HistoryModel> CREATOR = new Creator<HistoryModel>() {
        @Override
        public HistoryModel createFromParcel(Parcel in) {
            return new HistoryModel(in);
        }

        @Override
        public HistoryModel[] newArray(int size) {
            return new HistoryModel[size];
        }
    };

    public String getTotalBuy() {
        return totalBuy;
    }

    public void setTotalBuy(String totalBuy) {
        this.totalBuy = totalBuy;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getQuantity() {
        return quantity;
    }

    public void setQuantity(String quantity) {
        this.quantity = quantity;
    }

    public String getTotalItem() {
        return totalItem;
    }

    public void setTotalItem(String totalItem) {
        this.totalItem = totalItem;
    }

    public String getPrice() {
        return price;
    }

    public void setPrice(String price) {
        this.price = price;
    }

    public String getTrxNumber() {
        return trxNumber;
    }

    public void setTrxNumber(String trxNumber) {
        this.trxNumber = trxNumber;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
