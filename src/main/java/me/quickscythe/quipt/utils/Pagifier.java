package me.quickscythe.quipt.utils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Pagifier {

    private final List<Object> OBJECTS = new ArrayList<>();
    private final int MAX_ITEMS;
    private final Map<Integer, List<Object>> PAGES;


    public Pagifier(List<?> objects, int max_items) {

        MAX_ITEMS = max_items;
        PAGES = new HashMap<>();

        int items = 0;
        int total_items = 0;
        int page = 1;

        for (Object s : objects) {
            OBJECTS.add(s);
            items= items+1;
            total_items = total_items+1;
            if(!PAGES.containsKey(page))
                PAGES.put(page,new ArrayList<>());
            PAGES.get(page).add(s);
            if(items >= max_items){
                page = page+1;
                items = 0;
            }

        }

    }

    public List<?> getPage(int page){
        return PAGES.getOrDefault(page, PAGES.get(1));
    }

    public int getItemsPerPage(){
        return MAX_ITEMS;
    }

    public List<?> getAllObjects(){
        return OBJECTS;
    }

    public int getPages() {
        return PAGES.size();
    }
}
