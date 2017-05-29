package com.arihantmartadmin.jay.arihantmart_admin;


public class ItemObject_gridview {

    private String content;
    private String imageResource;

    public ItemObject_gridview(String content, String imageResource) {
        this.content = content;
        this.imageResource = imageResource;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getImageResource() {
        return imageResource;
    }

    public void setImageResource(String imageResource) {
        this.imageResource = imageResource;
    }
}