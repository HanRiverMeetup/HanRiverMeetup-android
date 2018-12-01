package com.depromeet.hanriver.hanrivermeetup.fragment.meeting.map;

public class CardItem {
    private String mTextResource;
    private String mTitleResource;
    private String mHomepageUrl;
    private String mTel;
    private String imageUrl;

    public CardItem(String title, String text,String url,String tel,String imageUrl) {
        mTitleResource = title;
        mTextResource = text;
        mHomepageUrl = url;
        mTel = tel;
        this.imageUrl = imageUrl;
    }

    public String getText() {
        return mTextResource;
    }

    public String getTitle() {
        return mTitleResource;
    }

    public String getmHomepageUrl() {
        return mHomepageUrl;
    }

    public String getmTel() {
        return mTel;
    }

    public String getImageUrl() {
        return imageUrl;
    }
}
