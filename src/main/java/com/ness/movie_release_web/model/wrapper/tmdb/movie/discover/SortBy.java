package com.ness.movie_release_web.model.wrapper.tmdb.movie.discover;

public enum SortBy {

    popularity_desc("popularity", Order.desc),
    popularity_asc("popularity", Order.asc),
    release_date_desc("release_date", Order.desc),
    release_date_asc("release_date", Order.asc),
    revenue_desc("revenue", Order.desc),
    revenue_asc("revenue", Order.asc),
    original_title_desc("original_title", Order.desc),
    original_title_asc("original_title", Order.asc),
    vote_average_desc("vote_average", Order.desc),
    vote_average_asc("vote_average", Order.asc);

    private String type;
    private Order order;

    SortBy(String type, Order order) {
        this.type = type;
        this.order = order;
    }

    public String getSearchString() {
        return this.type + "." + order;
    }

    public String getType() {
        return type;
    }

    public Order getOrder() {
        return order;
    }
}

enum Order {
    asc("to higher"),
    desc("to lower");

    private String type;

    Order(String type) {
        this.type = type;
    }

    public String getType() {
        return type;
    }
}
