package org.coderocks.audio;

import org.vaadin.addon.audio.server.AudioPlayer;
import org.vaadin.addon.audio.server.effects.FilterEffect;
import org.vaadin.addon.audio.server.state.PlaybackState;
import org.vaadin.addon.audio.server.state.StateChangeCallback;
import org.vaadin.addon.audio.server.state.StreamState;
import org.vaadin.addon.audio.server.state.StreamStateCallback;
import org.vaadin.addon.audio.shared.util.Log;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.Slider;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class Controls extends VerticalLayout {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static final int SKIP_TIME_SEC = 0;
	private static final String BUTTON_SIZE_CLASS = "small";
	private AudioPlayer player;
	Button fwdButton;
	Button stopButton;
	Button rewButton;
	Button pauseButton;
	Button playButton;
	ComboBox preloadSelect;
	Slider volumeSlider;
	AudioPositionSlider positionSlider;

	public Controls(AudioPlayer player, String streamName) {

		setSpacing(true);
		setMargin(true);
		setSizeFull();

		this.player = player;

		positionSlider = new AudioPositionSlider("Position");
		positionSlider.setSizeFull();
		positionSlider.setEnabled(true);
		positionSlider.addValueChangeListener(e -> {
			player.setPosition(positionSlider.getValue().intValue());
		});
		addComponent(positionSlider);

		VerticalLayout innerContainer = new VerticalLayout();
		innerContainer.setWidth("100%");
		innerContainer.setSpacing(true);

		HorizontalLayout buttonLayout = new HorizontalLayout();
		buttonLayout.setSpacing(true);

		buttonLayout.addComponent(rewButton = new Button("Back " + SKIP_TIME_SEC + " sec", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				player.skip(-SKIP_TIME_SEC * 1000);
			}
		}));
		rewButton.addStyleName(BUTTON_SIZE_CLASS);

		buttonLayout.addComponent(stopButton = new Button("Stop", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				player.stop();
			}
		}));
		stopButton.addStyleName(BUTTON_SIZE_CLASS);

		buttonLayout.addComponent(pauseButton = new Button("Pause", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (player.isPaused()) {
					player.resume();
				} else {
					player.pause();
				}
			}
		}));
		pauseButton.addStyleName(BUTTON_SIZE_CLASS);

		buttonLayout.addComponent(playButton = new Button("Play", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				if (player.isStopped()) {
					player.play();
				} else if (player.isPaused()) {
					player.resume();
				} else {
					// player.play(0);
					player.play();
				}
			}
		}));
		playButton.addStyleName(BUTTON_SIZE_CLASS);

		buttonLayout.addComponent(fwdButton = new Button("Forward " + SKIP_TIME_SEC + " sec", new ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				player.skip(SKIP_TIME_SEC * 1000);
			}
		}));
		fwdButton.addStyleName(BUTTON_SIZE_CLASS);

		buttonLayout.addComponent(preloadSelect = new ComboBox("Preload Chunks"));
		preloadSelect.addItems(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		preloadSelect.select(player.getNumberChunksToPreload());
		preloadSelect.addValueChangeListener(new ValueChangeListener() {
			@Override
			public void valueChange(ValueChangeEvent event) {
				int val = (int) preloadSelect.getValue();
				player.setNumberChunksToPreload(val);
			}
		});

		innerContainer.addComponent(buttonLayout);
		innerContainer.setComponentAlignment(buttonLayout, Alignment.MIDDLE_CENTER);

		HorizontalLayout sliderLayout = new HorizontalLayout();
		sliderLayout.setSpacing(true);

		Slider volumeSlider;
		sliderLayout.addComponent(volumeSlider = new Slider("Volume"));
		volumeSlider.setImmediate(true);
		volumeSlider.setMin(0);
		volumeSlider.setMax(100);
		volumeSlider.setValue(80d);
		volumeSlider.setWidth("150px");
		volumeSlider.addValueChangeListener(e -> {
			final double volume = volumeSlider.getValue() / 100d;
			player.setVolume(volume);
		});

		// =========================================================================
		// === Individual Channel Gain Example =====================================
		// =========================================================================

		Slider leftChannelGain;
		sliderLayout.addComponent(leftChannelGain = new Slider("L"));
		leftChannelGain.setImmediate(true);
		leftChannelGain.setMin(0);
		leftChannelGain.setMax(100);
		leftChannelGain.setValue(80d);
		leftChannelGain.setWidth("150px");
		leftChannelGain.addValueChangeListener(e -> {
			final double volume = leftChannelGain.getValue() / 100d;
			player.setVolumeOnChannel(volume, 0);
		});

		Slider rightChannelGain;
		sliderLayout.addComponent(rightChannelGain = new Slider("R"));
		rightChannelGain.setImmediate(true);
		rightChannelGain.setMin(0);
		rightChannelGain.setMax(100);
		rightChannelGain.setValue(80d);
		rightChannelGain.setWidth("150px");
		rightChannelGain.addValueChangeListener(e -> {
			final double volume = rightChannelGain.getValue() / 100d;
			player.setVolumeOnChannel(volume, 1);
		});

		// =========================================================================
		// === Auto Adjusting Channel Gain Example =================================
		// =========================================================================

		// final HorizontalLayout channelGainSliders = new HorizontalLayout();
		// sliderLayout.addComponent(channelGainSliders);
		//
		// player.addValueChangeListener(new VolumeChangeCallback() {
		// @Override
		// public void onVolumeChange(double volume, double[] channelVolumes) {
		// Log.message(this, "onVolumeChange callback fired");
		// int numChannels = channelVolumes.length;
		// int numSliders = channelGainSliders.getComponentCount();
		// int numSlidersToAdd = numChannels - numSliders;
		// Log.message(this, "Sliders: " + numChannels + ", " + numSliders + ", " +
		// numSlidersToAdd);
		// // remove extra sliders if needed
		// for (int i = numSlidersToAdd; i < 0; i++) {
		// Log.message(this, "Removing channel slider");
		// int sliderIndex = numSliders + i;
		// channelGainSliders.removeComponent(channelGainSliders.getComponent(sliderIndex));
		// }
		// // create additional sliders if needed
		// for (int i = 0; i < numSlidersToAdd; i++) {
		// Log.message(this, "Adding channel slider");
		// int sliderIndex = numSliders + i;
		// final Slider channelGain = new Slider("Channel " + sliderIndex + " Volume");
		// channelGain.setImmediate(true);
		// channelGain.setMin(0);
		// channelGain.setMax(100);
		// channelGain.setValue(channelVolumes[i]);
		// channelGain.setWidth("150px");
		// // assign listener so this slider controls the corresponding channel's gain
		// channelGain.addValueChangeListener(e -> {
		// final double gain = channelGain.getValue() / 100d;
		// player.setVolumeOnChannel(gain, sliderIndex);
		// });
		// // add channel gain slider to our group of sliders
		// channelGainSliders.addComponent(channelGain);
		// }
		// }
		// });

		Slider balanceSlider;
		sliderLayout.addComponent(balanceSlider = new Slider("Balance"));
		balanceSlider.setImmediate(true);
		balanceSlider.setWidth("150px");
		balanceSlider.setMin(-100);
		balanceSlider.setMax(100);
		balanceSlider.setValue(0d);
		balanceSlider.addValueChangeListener(e -> {
			final double balance = balanceSlider.getValue() / 10d;
			player.setBalance(balance);
		});

		Slider speedSlider;
		sliderLayout.addComponent(speedSlider = new Slider("Speed"));
		speedSlider.setImmediate(true);
		speedSlider.setWidth("150px");
		speedSlider.setResolution(1);
		speedSlider.setMin(0.5d);
		speedSlider.setMax(3);
		speedSlider.setValue(1d);
		speedSlider.addValueChangeListener(e -> {
			final double playbackSpeed = speedSlider.getValue();
			player.setPlaybackSpeed(playbackSpeed);
		});

		FilterEffect filterEffect = new FilterEffect();
		sliderLayout.addComponent(createFilterEffectElement(player, filterEffect));

		innerContainer.addComponent(sliderLayout);
		innerContainer.setComponentAlignment(sliderLayout, Alignment.MIDDLE_CENTER);

		addComponent(innerContainer);

		//
		// Stream delete controls
		//

		Button deleteButton = new Button("Delete stream", new Button.ClickListener() {
			@Override
			public void buttonClick(ClickEvent event) {
				// TODO: actually delete the stream and this component...
				Log.message(this, "delete stream");
				Notification.show("Feature not implemented yet!", Notification.Type.ERROR_MESSAGE);
			}
		});
		deleteButton.addStyleName("danger");
		deleteButton.addStyleName(BUTTON_SIZE_CLASS);

		final UI ui = UI.getCurrent();
		final Label streamStatus = new Label("Stream status: IDLE");
		streamStatus.setSizeFull();
		player.getStream().addStateChangeListener(new StreamStateCallback() {
			@Override
			public void onStateChanged(final StreamState newState) {
				ui.access(new Runnable() {
					@Override
					public void run() {
						String text = "Stream status: ";
						switch (newState) {
						case COMPRESSING:
							text += "COMPRESSING";
							break;
						case ENCODING:
							text += "ENCODING";
							break;
						case IDLE:
							text += "IDLE";
							break;
						case READING:
							text += "READING";
							break;
						case SERIALIZING:
							text += "SERIALIZING";
							break;
						default:
							text += "broken or something";
							break;
						}
						streamStatus.setValue(text);
					}
				});
			}
		});

		final Label playerStatus = new Label("Player status: STOPPED");
		playerStatus.setSizeFull();
		player.addStateChangeListener(new StateChangeCallback() {
			@Override
			public void playbackStateChanged(final PlaybackState new_state) {
				ui.access(new Runnable() {
					@Override
					public void run() {
						String text = "Player status: ";
						switch (new_state) {
						case PAUSED:
							text += "PAUSED";
							break;
						case PLAYING:
							text += "PLAYING";
							break;
						case STOPPED:
							text += "STOPPED";
							break;
						default:
							break;
						}
						playerStatus.setValue(text);
					}
				});
			}

			@Override
			public void playbackPositionChanged(final int new_position_millis) {
				ui.access(new Runnable() {
					@Override
					public void run() {
						// TODO: for proper slider setting, we need to know the position
						// in millis and total duration of audio
						int duration = getPlayer().getDuration();
						int pos = getPlayer().getPosition();
						if (pos >= duration) {
							positionSlider.setValueSecretly(0d);
							getPlayer().stop();
							positionSlider.setCaption("00:00 / " + player.getDurationString());
							stopButton.focus();
							return;
						}
						positionSlider.setMax(duration);
						positionSlider.setMin(0);
						// set value without trigger value change event
						positionSlider.setValueSecretly((double) new_position_millis);
						positionSlider.setCaption(player.getPositionString() + " / " + player.getDurationString());
					}
				});
			}
		});

		HorizontalLayout bottomLayout = new HorizontalLayout();
		bottomLayout.setWidth("100%");

		bottomLayout.addComponent(playerStatus);
		bottomLayout.setComponentAlignment(playerStatus, Alignment.MIDDLE_LEFT);

		bottomLayout.addComponent(streamStatus);
		bottomLayout.setComponentAlignment(streamStatus, Alignment.MIDDLE_LEFT);

		bottomLayout.addComponent(deleteButton);
		bottomLayout.setComponentAlignment(deleteButton, Alignment.MIDDLE_RIGHT);
		innerContainer.addComponent(bottomLayout);

	}

	public AudioPlayer getPlayer() {
		return player;
	}

	public Slider getPositionSlider() {
		return positionSlider;
	}

	public Button getRewButton() {
		return rewButton;
	}

	public Button getStopButton() {
		return stopButton;
	}

	public Button getPauseButton() {
		return pauseButton;
	}

	public Button getPlayButton() {
		return playButton;
	}

	public Button getFwdButton() {
		return fwdButton;
	}

	protected static HorizontalLayout createEffectContainer(String label) {
		HorizontalLayout effectUi = new HorizontalLayout();
		effectUi.setSpacing(true);
		effectUi.setSizeFull();
		effectUi.setDefaultComponentAlignment(Alignment.MIDDLE_LEFT);
		effectUi.setCaption(label);
		return effectUi;
	}

	protected static HorizontalLayout createFilterEffectElement(AudioPlayer player, FilterEffect filterEffect) {
		// set filter defaults
		filterEffect.setType(FilterEffect.Type.LOWPASS);
		filterEffect.setFrequency(20000);
		player.addEffect(filterEffect);
		// build filter ui component
		HorizontalLayout effectUi = createEffectContainer("Filter Effect");
		OptionGroup typeSelector = new OptionGroup();
		effectUi.addComponent(typeSelector);
		typeSelector.setImmediate(true);
		typeSelector.addItems(FilterEffect.Type.HIGHPASS, FilterEffect.Type.LOWPASS);
		typeSelector.setItemCaption(FilterEffect.Type.HIGHPASS, "High pass");
		typeSelector.setItemCaption(FilterEffect.Type.LOWPASS, "Low pass");
		typeSelector.select(filterEffect.getType());
		typeSelector.addValueChangeListener(e -> {
			Log.message(player, "Set filter to " + ((FilterEffect.Type) typeSelector.getValue()));
			filterEffect.setType((FilterEffect.Type) typeSelector.getValue());
			player.updateEffect(filterEffect);
		});
		Slider frequency = new Slider();
		effectUi.addComponent(frequency);
		frequency.setImmediate(true);
		frequency.setMax(20000);
		frequency.setMin(0);
		frequency.setWidth("250px");
		frequency.setValue(filterEffect.getFrequency());
		frequency.addValueChangeListener(e -> {
			double freqVal = frequency.getValue();
			filterEffect.setFrequency(freqVal);
			player.updateEffect(filterEffect);
			Log.message(player, "Frequency set to " + freqVal);
		});
		effectUi.setExpandRatio(frequency, 1);
		return effectUi;
	}
}