package com.mieczkowskidev.friendspotter.Utils;

import com.trnql.smart.places.PlaceEntry;

import java.util.List;

/**
 * Created by Patryk Mieczkowski on 2015-12-30
 */
public interface PlaceInterface {

    void onPlaceUpdate(List<PlaceEntry> placeEntryList);
}
