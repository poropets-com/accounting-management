package com.gregad.accountingmanagement.service.interfaces;

import java.util.List;

public interface IFavoritesService {
    List<String> addFavorite(String email, String postId , String token);
    List<String> removeFavorite(String email, String postId , String token);
    List<String> getFavorites(String email);
}
