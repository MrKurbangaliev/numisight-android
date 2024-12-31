package com.iremia.app.numisight.helper;

import com.iremia.app.numisight.R;

public class IconAssignment {
    private final String mItem;

    public IconAssignment(String item) {
        this.mItem = item;
    }

    public int getIconRes() {
        int iconRes;
        switch (mItem) {
            case "Food":
                iconRes = R.drawable.icons8_hamburger;
                break;
            case "Medicine":
                iconRes = R.drawable.icons8_medicine;
                break;
            case "Sweets":
                iconRes = R.drawable.icons8_ice_cream_cone;
                break;
            case "Hygiene products":
                iconRes = R.drawable.icons8_toilet_paper;
                break;
            case "Car":
                iconRes = R.drawable.icons8_car;
                break;
            case "Rent":
                iconRes = R.drawable.icons8_rent;
                break;
            case "Pet care":
                iconRes = R.drawable.icons8_cat_footprint;
                break;
            case "Books":
                iconRes = R.drawable.icons8_book;
                break;
            case "Education":
                iconRes = R.drawable.icons8_read;
                break;
            case "Commission":
                iconRes = R.drawable.icons8_coin;
                break;
            case "Public utilities":
                iconRes = R.drawable.icons8_city;
                break;
            case "Phone bill":
                iconRes = R.drawable.icons8_phone;
                break;
            case "Tithe":
                iconRes = R.drawable.icons8_charity;
                break;
            case "Tips":
                iconRes = R.drawable.icons8_tips;
                break;
            case "Transport":
                iconRes = R.drawable.icons8_transportation;
                break;
            case "Salary":
                iconRes = R.drawable.icons8_salary;
                break;
            case "Balance correction":
                iconRes = R.drawable.icons8_general_ledger;
                break;
            case "Currency exchange":
                iconRes = R.drawable.icons8_currency_exchange;
                break;
            case "Financial help":
                iconRes = R.drawable.icons8_recieve;
                break;
            case "Bike":
                iconRes = R.drawable.icons8_bicycle;
                break;
            case "Services":
                iconRes = R.drawable.icons8_service;
                break;
            case "Subscriptions":
                iconRes = R.drawable.icons8_renew;
                break;
            case "Tools":
                iconRes = R.drawable.icons8_tools;
                break;
            case "Sale":
                iconRes = R.drawable.icons8_sale;
                break;
            default:
                iconRes = R.drawable.icons8_other;
        }

        return iconRes;
    }
}
