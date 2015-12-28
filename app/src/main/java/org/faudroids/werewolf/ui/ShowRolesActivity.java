package org.faudroids.werewolf.ui;


import android.graphics.Path;
import android.os.Bundle;
import android.support.annotation.AnimRes;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eftimoff.androipathview.PathView;

import org.faudroids.werewolf.R;
import org.faudroids.werewolf.core.GameManager;
import org.faudroids.werewolf.core.Player;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import roboguice.inject.ContentView;
import roboguice.inject.InjectView;
import timber.log.Timber;

@ContentView(R.layout.activity_show_roles)
public class ShowRolesActivity extends AbstractActivity {

	@InjectView(R.id.layout_instructions) private View instructionsLayout;
	@InjectView(R.id.txt_player_name) private TextView playerNameText;
	@InjectView(R.id.btn_reveal) private View revealButton;

	@InjectView(R.id.layout_role) private View roleLayout;
	@InjectView(R.id.img_icon) private PathView iconView;
	@InjectView(R.id.txt_role_name) private TextView roleNameText;
	@InjectView(R.id.txt_role_description) private TextView roleDescriptionText;

	@InjectView(R.id.layout_nav) private View navLayout;
	@InjectView(R.id.btn_back) private View backButton;
	@InjectView(R.id.btn_next) private View nextButton;

	@Inject private GameManager gameManager;
	private List<Player> players;
	private int currentPlayerIdx;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		// get current player
		players = gameManager.loadPlayers();
		int idx = -1;
		for (int i = 0; i < players.size(); ++i) {
			if (!players.get(i).isSeen()) {
				idx = i;
				break;
			}
		}
		if (idx == -1) {
			Timber.d("all players have seen their role");
			finish();
			return;
		}
		setCurrentPlayerIdx(idx, false);

		// start pulse animation on reveal button
		revealButton.startAnimation(loadAnimation(R.anim.pulse));

		// setup click to reveal
		revealButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				revealButton.setEnabled(false);

				// update player
				Player player = players.get(currentPlayerIdx);
				player.setIsSeen(true);
				gameManager.savePlayers(players);

				// toggle layouts
				instructionsLayout.startAnimation(loadAnimation(R.anim.fade_out));
				navLayout.startAnimation(loadAnimation(R.anim.fade_out));
				roleLayout.startAnimation(loadAnimation(R.anim.fade_in));
				roleLayout.setVisibility(View.VISIBLE);

				// show role text
				roleNameText.setText(player.getRole().getNameId());
				roleDescriptionText.setText(player.getRole().getGoalId());

				// show role icon
				assertPathView();
				iconView.setSvgResource(player.getRole().getIconId());
				iconView.setPaths(new ArrayList<Path>());
				iconView.getPathAnimator()
						.delay(100)
						.duration(1000)
						.interpolator(new AccelerateDecelerateInterpolator())
						.start();

				instructionsLayout.postDelayed(new Runnable() {
					@Override
					public void run() {
						revealButton.setEnabled(true);

						// toggle layouts
						instructionsLayout.startAnimation(loadAnimation(R.anim.fade_in));
						navLayout.startAnimation(loadAnimation(R.anim.fade_in));
						roleLayout.startAnimation(loadAnimation(R.anim.fade_out));

						// enable showing next player role
						nextButton.setEnabled(true);
					}
				}, 3000);

			}
		});

		// set nav buttons
		nextButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				instructionsLayout.startAnimation(loadAnimation(R.anim.move_forward));
				setCurrentPlayerIdx(currentPlayerIdx + 1, true);
			}
		});
		backButton.setOnClickListener(new View.OnClickListener() {
			@Override
			public void onClick(View v) {
				instructionsLayout.startAnimation(loadAnimation(R.anim.move_backward));
				setCurrentPlayerIdx(currentPlayerIdx - 1, true);
			}
		});
	}


	private void setCurrentPlayerIdx(int currentPlayerIdx, boolean delaySetPlayerName) {
		this.currentPlayerIdx = currentPlayerIdx;
		if (currentPlayerIdx >= players.size()) {
			Timber.d("done viewing all players");
			finish();
			return;
		}

		final Player player = players.get(currentPlayerIdx);
		nextButton.setEnabled(player.isSeen());
		backButton.setVisibility(currentPlayerIdx != 0 ? View.VISIBLE : View.GONE);
		int delay = delaySetPlayerName ? getResources().getInteger(R.integer.anim_swipe_role_duration) : 0;
		playerNameText.postDelayed(new Runnable() {
			@Override
			public void run() {
				Timber.d("player name " + player.getName());
				playerNameText.setText(player.getName());
			}
		}, delay);
	}


	/**
	 * Hack to reset the path view internal state, since setting the svg source
	 * only works when setting it the first time.
	 */
	private void assertPathView() {
		ViewGroup parent = getParent(iconView);
		PathView newIconView = (PathView) getLayoutInflater().inflate(R.layout.role_icon, null).findViewById(R.id.img_icon);
		final int index = parent.indexOfChild(iconView);
		removeView(iconView);
		removeView(newIconView);

		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
				getResources().getDimensionPixelSize(R.dimen.role_icon_size),
				getResources().getDimensionPixelSize(R.dimen.role_icon_size));
		layoutParams.gravity = Gravity.CENTER_HORIZONTAL;
		parent.addView(newIconView, index, layoutParams);
		iconView = newIconView;
	}


	private void removeView(View view) {
		ViewGroup parent = getParent(view);
		if(parent != null) {
			parent.removeView(view);
		}
	}


	private ViewGroup getParent(View view) {
		return (ViewGroup)view.getParent();
	}


	private Animation loadAnimation(@AnimRes int animationRes) {
		return AnimationUtils.loadAnimation(this, animationRes);
	}

}
