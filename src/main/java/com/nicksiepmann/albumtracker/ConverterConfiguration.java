/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.nicksiepmann.albumtracker;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.cloud.spring.data.datastore.core.convert.DatastoreCustomConversions;
import java.time.LocalDateTime;
import java.util.Arrays;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.convert.converter.Converter;

/**
 *
 * @author Nick.Siepmann
 */
@Configuration
public class ConverterConfiguration {

    @Bean
    public DatastoreCustomConversions datastoreCustomConversions() {
        return new DatastoreCustomConversions(
                Arrays.asList(
                        SONG_STRING_CONVERTER,
                        STRING_SONG_CONVERTER,
                        COMMENT_STRING_CONVERTER,
                        STRING_COMMENT_CONVERTER));
    }
    static final Converter<Song, String> SONG_STRING_CONVERTER
            = new Converter<Song, String>() {
        @Override
        public String convert(Song song) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(song);
//                System.out.println("ResultingJSONstring = " + json);
                return json;
                //System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "";
        }
    };

    //Converters to read custom Album type
    static final Converter<String, Song> STRING_SONG_CONVERTER
            = new Converter<String, Song>() {
        @Override
        public Song convert(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Song song = mapper.readValue(json, Song.class);
//                System.out.println("ConvertedSong = " + song.toString());
                return song;
                //System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    };
    static final Converter<Comment, String> COMMENT_STRING_CONVERTER
            = new Converter<Comment, String>() {
        @Override
        public String convert(Comment comment) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                String json = mapper.writeValueAsString(comment);
//                System.out.println("ResultingJSONstring = " + json);
                return json;
                //System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return "";
        }
    };

    //Converters to read custom Comment type
    static final Converter<String, Comment> STRING_COMMENT_CONVERTER
            = new Converter<String, Comment>() {
        @Override
        public Comment convert(String json) {
            ObjectMapper mapper = new ObjectMapper();
            try {
                Comment comment = mapper.readValue(json, Comment.class);
//                System.out.println("ConvertedSong = " + song.toString());
                return comment;
                //System.out.println(json);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return null;
        }
    };

}
