package com.gregad.accountingmanagement.rest;
import com.gregad.accountingmanagement.service.interfaces.IFavoritesService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.parameters.P;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.gregad.accountingmanagement.api.ApiConstants.*;

@RestController
public class PostFavoritesController {
    @Autowired
    IFavoritesService favoritesService;
    
    @PutMapping(value = PREFIX+"/{email}"+FAVORITE+"/{postId}")
    List<String> addFavorite(@RequestHeader(name="Authorization") String token,
                             @PathVariable String email,
                             @PathVariable String postId){
        return favoritesService.addFavorite(email,postId,token);
    }
    
    @DeleteMapping(value = PREFIX+"/{email}"+FAVORITE+"/{postId}")
    List<String> removeFavorite(@RequestHeader(name="Authorization") String token,
                             @PathVariable String email,
                             @PathVariable String postId){
        return favoritesService.removeFavorite(email,postId,token);
    }
    
    @GetMapping(value = PREFIX+"/{email}"+FAVORITES)
    List<String> getUserFavorites(@PathVariable String email){
        return favoritesService.getFavorites(email);
    }
}
