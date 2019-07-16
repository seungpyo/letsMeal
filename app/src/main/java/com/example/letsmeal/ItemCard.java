package com.example.letsmeal;

public class ItemCard {
    String title;
    int image;

    int getImage() {
        return this.image;
    }

    String getTitle() {
        return this.title;
    }

    ItemCard(int image, String title) {
        this.image = image;
        this.title = title;
    }
}
