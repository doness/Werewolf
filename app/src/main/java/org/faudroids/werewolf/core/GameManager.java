package org.faudroids.werewolf.core;

import android.content.Context;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.faudroids.werewolf.R;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import javax.inject.Inject;

import timber.log.Timber;

/**
 * Created by dex on 12/28/15.
 */
public class GameManager {

    private final String PLAYERS_FILENAME = "Players";

    private final Context context;
	private final Gson gson = new Gson();
	private final Type playersType = new TypeToken<List<Player>>(){}.getType();

    @Inject
    GameManager(Context context){
        this.context = context;
    }


    public List<Player> loadPlayers(){
        try {
			return gson.fromJson(new FileReader(new File(context.getFilesDir(), PLAYERS_FILENAME)), playersType);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }


	public boolean savePlayers(List<Player> players) {
		String json = gson.toJson(players, playersType);
		FileOutputStream fos;
		try {
			fos = context.openFileOutput(PLAYERS_FILENAME, Context.MODE_PRIVATE);
			fos.write(json.getBytes());
			fos.close();
		} catch (IOException e) {
			Timber.e(e, "failed to save players");
			return false;
		}
		return true;
	}


	public void createRandomTestPlayers(int playerCount) {
		Random random = new Random();
		List<Player> players = new ArrayList<>();
		for(int i = 0; i < playerCount; ++i) {
			int randomRole = random.nextInt(Role.values().length);
			players.add(new Player(i, false, Role.values()[randomRole], context.getString(R.string.default_player_name, (i + 1))));
		}
		savePlayers(players);
	}

}
