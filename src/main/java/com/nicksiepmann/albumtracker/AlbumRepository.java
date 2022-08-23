/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Interface.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.google.cloud.spring.data.datastore.repository.DatastoreRepository;
import java.util.List;

/**
 *
 * @author Nick.Siepmann
 */
public interface AlbumRepository extends DatastoreRepository<Album, Long> {

    List<Album> findByName(String name);

    List<Album> findByArtist(String artist);
    
    List<Album> findByNameAndArtist(String name, String artist);


}
