package com.gorgo.pirates.controller;

import java.util.Random;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.util.Log;
import android.util.SparseArray;

import com.gorgo.pirates.Dialogs;
import com.gorgo.pirates.R;
import com.gorgo.pirates.GameThread;
import com.gorgo.pirates.model.Enemy;
import com.gorgo.pirates.model.Guybrush;
import com.gorgo.pirates.view.SpriteTile;
import com.gorgo.pirates.view.Text;

/**
 * @author Gorgo
 * 
 * La classe Turns gestisce tutti i turni di gioco, i turni durante l'introduzione/outro ed è il motore principale del gioco
 * 
 */

public class Turns {
	
	private static final String TAG = Turns.class.getSimpleName();
	
	// Turni
	private final int
		INIT_SCABB_INTRO = -8,
		INIT_SCABB_FUOCO = -7,
		SCABB_FUOCO = -6,
		INIT_MAP = -3,
		INIT_MAP_POST_INTRO = -2,
		INIT_ROUND = -1,
			
		ROUND_TALK_0 = 0,
		ROUND_TALK_1 = 1,
		ROUND_TALK_2 = 2,
		ROUND_TALK_3 = 3,
		ROUND_TALK_4 = 4,
			
		ROUND_0 = 5,
		ROUND_1_USER_INSULTI = 6, // User sceglie insulto
		ROUND_2_PIRATE_CHECK_ANSWER = 7, // Pirata calcola risposta
		ROUND_3 = 8,
		ROUND_4 = 9,
		ROUND_5_PIRATE_TURN = 10, // Tocca al Pirata
		ROUND_6_USER_CONTROINSULTI = 11, // User sceglie controInsulto
		ROUND_7 = 12,
		ROUND_8_CHECK_CONTROINSULTO = 13,
		ROUND_9 = 14,
		ROUND_10 = 15,
		ROUND_11_CHECK_WINNER = 16,
		ROUND_12_CHECK_WINNER = 17,
		ROUND_13_OUTRO_MATCH = 18, //Se Carla parte l'outro altrimenti Guybrush scappa
		RELOAD_MAP = 19,
		OUTRO_FUOCO = 20;
	
	// Costanti introFuoco personaggi
	private static final int 
		GUYBRUSH = 1,
		PIRATA_SX = 2,
		PIRATA_DX = 3;
		
	// Costanti Scritte IntroFuoco
	private final int
		SCABB_INTRO_SIZE = 30,
		SCABB_INTRO_TIME = 9000,
		SCABB_FUOCO_TIME = 3000;
		
	// Per sfidare carla bisogna aver vinto almeno MINIMO_VITTORIE_READY_CARLA volte contro i pirati
	private final int
		MINIMO_VITTORIE_READY_CARLA = 4;
		
		// Per sfidare Carla bisogna aver imparato almeno il PERC_MIN_INSULTI_READY_CARLA % degli insulti
	private final double
		PERC_MIN_INSULTI_READY_CARLA = 0.6; // *100
		
		// Costanti utils classe Turns
	private final int
		CARLA_INIT_FINISHED = -1, // Visualizzato l'intro di Carla
		NO_PIRATE_SELECTED = -1, // Non è ancora possibile selezionare un pirata
		NO_SKIPPABLE = -1, // Non è più possibile skippare l'intro
		NO_TYPE_SELECTED = 0, // Il type del Pirata è resettato
		NO_LEVEL_SELECTED = 0, // Non è stato selezionato nessun livello
		WAITING_PIRATE = 0, // Il gioco è in attesa che venga scelto un pirata (Mappa)
		ANSWER_INIT_ROUND = 0, // Risposta per iniziare il duello
		NO_LOCK = 0, // I personaggi vengono disegnati
		SI_LOCK = 1, // I personaggi NON vengono disegnati
		NO_INTRO = 0, // Al controller segnalo che l'introFuoco è finito
		NO_CARLA_MAP = 0, // Non è ancora possibile sfidare Carla
		SI_CARLA_MAP = 1, // E' possibile sfidare Carla
		LEAVE_INTRO_SPEECH = 3, // Risposta per abbandonare il pirata
		END_GAME = 1; // Il gioco è finito
		
	// Risposte standard
	private static final int
		PIRATE_INCREDULO = -1,
		MAMMA_BRUTTO = 33,
		ARRR = 34,
		ARRENDO_INSULTI = 35,
				
		OH_YEAH = 33,
		GOMMA_COLLA = 34,
		TREMO = 35,
				
		RIPETERE = 36,
		ARRENDO_CONTROINSULTI = 37;

			
	// Costanti pirati duelli
	private static final int
		PIRATA_BIONDO = 1,
		PIRATA_CAPPELLO = 2,
		PIRATA_BANDANA = 3,
		PIRATA_PELATO = 4,
		CARLA = 5;
			
	// Intro modalità
	private final int 
		INTRO_SCABB = 1,
		FUOCO_INIZIO = 2,
		FUOCO_FINE = 3,
		CREDITS = 4;
		
	//Costanti Handler
	private static final int 
		LVIEW_VISIBLE = 1, 
		INIT_INSULTS = 2,
		LISTVIEW_UPDATE = 3,
		INIT_CONTROINSULTS = 4,
		INIT_GUYBRUSH = 5,
		INIT_GUYBRUSH_CARLA = 6,
		MUSIC_PAUSE = 7;
	
	// Controller
	private GuybrushController controllerG;
	private EnemyController controllerE;
	private WorldController controllerW;
	
	// Audio
	private MediaPlayer mPlayer;
	private SoundPlayer soundPlayer;

	// Classi Utils
	private Dialogs dialogs;
	private GameThread mainThread;
	private Enemy enemy;
	private Guybrush guybrush;
	
	// Insulti temporanei
	// Durante il round vengono modificati inserendo/rimuovendo elementi
	// Vengono rinizializzati ogni round con le apposite init()
	private SparseArray<String> insultiCurrentGuybrush, insultiCurrentEnemy, controInsultiGuybrush, insultiCurrentRemoved;

	// Indici Turni	
	private int turnDialog = INIT_SCABB_INTRO; // Turno PRINCIPALE gioco
	
	private int turnIntro = 0; // Turno Intro Pirati fuoco
	private int carlaInit = 0; // Turno "video" introduttivo Carla
	private int carlaSpeechIndex = 0; // Turno discorso introduttivo Carla
	private int carlaSpeechIndexOutro = 0; // Turno discorso finale Carla
	private int turnOutro = 0; // Turno Outro Guybrush Solo
	
	// Timer	
	private long carlaIntroTime; // Timer intro video Carla
	private long ScabbIntroTime; // Timer intro Scabb Isola
	private long guybrushOutroTime; // Timer Guybrush Vince Carla
	private long creditsTime; // Timer Credits
	
	// Testi
	private Text scabbTextIntro;
	private Text guybrushTextIntro;
	private Text pirate1TextIntro;
	private Text pirate2TextIntro;
	
	// SpriteID
	private int pirate_talk, pirate_fight, pirate_lose;
	
	// Sfondo
	private Bitmap background;
	
	// Utils
	private int GUYBRUSH_VINCE;
	private int PIRATE_VINCE;	
	private Context context;
	private int justSeenCarla;
	
	// String fuori dagli Array Discorsi
	private int INTRO_SCABB_STRING = R.string.intro_scabb;
	private int PIRATE_INCREDULO_STRING = R.string.pirata_incredulo;
	private int PIRATA_VINTO =  R.string.pirata_vinto;
	private int CARLA_VINCE =  R.string.carla_vince;
	private int GUYBRUSH_SCAPPA =  R.string.guybrush_scappa;
	private int READY_CARLA =  R.string.ready_carla;
	private int PIRATA_PERDE =  R.string.pirata_perde;
	
	
	

	public Turns (GuybrushController controllerGuybrush,
			EnemyController controllerEnemy, WorldController controllerWorld,
			Context context) {
		this.controllerG = controllerGuybrush;
		this.controllerE = controllerEnemy;
		this.controllerW = controllerWorld;
		this.context = context;
		
		//Inizializzo SparseArray per gli insulti già rimossi perchè detti, così da NON impararli ancora
		insultiCurrentRemoved = new SparseArray<String>();
		
		//Inizializzo Suoni
		soundPlayer = new SoundPlayer();
		soundPlayer.initSounds(context);
	}

	public void update(GameThread mainThread) {
		this.guybrush = controllerW.getGuybrush();
		this.enemy = controllerW.getEnemy();
		this.dialogs = controllerW.getDialogs();
		this.mainThread = mainThread;

		switch (turnDialog) {
		case INIT_SCABB_INTRO:
			ScabbIntroTime = System.currentTimeMillis();

			// Testo Intro
			scabbTextIntro = new Text(context);
			scabbTextIntro.setString(context.getResources().getString(INTRO_SCABB_STRING));
			scabbTextIntro.setSize(SCABB_INTRO_SIZE);
			scabbTextIntro.setTimeShow(SCABB_INTRO_TIME);
			scabbTextIntro.setColor(Color.MAGENTA);
			controllerW.setText(scabbTextIntro, 0);

			// Sfondo Intro
			background = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.scabb);
			controllerW.setSfondo(background);

			// Fuocherello Intro
			SpriteTile fuocherello = new SpriteTile(R.drawable.fuocherello,
					R.xml.fuocherello, context);
			fuocherello.setCurrentAnimation("fuocherello", true);
			controllerW.setSprite(fuocherello, 0);

			// Musica
			controllerW.setPlayer(MediaPlayer.create(context, R.raw.scabb));
			mPlayer = controllerW.getPlayer();
			mPlayer.start();

			// Imposto come intro l'isola di Scabb da lontano
			controllerW.setIntro(INTRO_SCABB);

			turnDialog++;
			break;

		case INIT_SCABB_FUOCO:
			//Vengono caricate tutte le risorse per l'introduzione/Tutorial
			
			if (System.currentTimeMillis() - ScabbIntroTime < 9000)
				break;
			else {
				// Ora l'utente può skippare l'intro
				controllerW.setSkipped(0);

				// Cambio Musica
				Music.changeMusic(context, mPlayer, R.raw.fuoco);

				// Cambio Sfondo
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.fuocoskip);
				controllerW.setSfondo(background);

				// GUYBRUSH SPRITE 1
				SpriteTile guybrush_fuoco = new SpriteTile(
						R.drawable.guybrush_fuoco, R.xml.guybrush_fuoco,
						context);
				guybrush_fuoco.setCurrentAnimation("guybrush_fuoco", true);
				controllerW.setSprite(guybrush_fuoco, 1);

				// PIRATE1 SPRITE 2
				SpriteTile pirata1_fuoco = new SpriteTile(
						R.drawable.pirata1_fuoco, R.xml.pirate1_fuoco, context);
				pirata1_fuoco.setCurrentAnimation("pirate1_fuoco", true);
				controllerW.setSprite(pirata1_fuoco, 2);
				pirata1_fuoco.setAnimationIdle();

				// PIRATE2 SPRITE 3
				SpriteTile pirata2_fuoco = new SpriteTile(
						R.drawable.pirata2_fuoco, R.xml.pirate2_fuoco, context);
				pirata2_fuoco.setCurrentAnimation("pirate2_dx", true);
				controllerW.setSprite(pirata2_fuoco, 3);
				pirata2_fuoco.setAnimationIdle();

				// FUOCONE SPRITE 4
				SpriteTile fuocone = new SpriteTile(R.drawable.fuocone,
						R.xml.fuocone, context);
				fuocone.setCurrentAnimation("fuocone", true);
				controllerW.setSprite(fuocone, 4);

				// TESTI
				guybrushTextIntro = new Text(context);
				guybrushTextIntro.setString("");
				guybrushTextIntro.setColor(Color.WHITE);
				guybrushTextIntro.setTimeShow(SCABB_FUOCO_TIME);
				controllerW.setText(guybrushTextIntro, 1);

				pirate1TextIntro = new Text(context);
				pirate1TextIntro.setString("");
				pirate1TextIntro.setColor(Color.RED);
				pirate1TextIntro.setTimeShow(SCABB_FUOCO_TIME);
				controllerW.setText(pirate1TextIntro, 2);

				pirate2TextIntro = new Text(context);
				pirate2TextIntro.setString("");
				pirate2TextIntro.setColor(Color.YELLOW);
				pirate2TextIntro.setTimeShow(SCABB_FUOCO_TIME);	
				controllerW.setText(pirate2TextIntro, 3);

				controllerW.setIntro(FUOCO_INIZIO);

				turnDialog++;
				break;
			}

		case SCABB_FUOCO:
			// Discorso introduttivo Guybrush e due pirati intorno al fuoco
			// Viene spiegato il meccanismo alla base del gioco e si sceglie il livello di difficoltà
			introFuoco();
			break;

		case INIT_MAP: {
			//Finito il discorso iniziale viene caricata la mappa con i pirati da scegliere e sfidare
			
			// Check mappa Carla
			if(controllerW.getOkCarla() == NO_CARLA_MAP){
				background = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.map1);
				controllerW.initMap(NO_CARLA_MAP);
			}
			else{
				background = BitmapFactory.decodeResource(context.getResources(),
						R.drawable.map2);
				controllerW.initMap(SI_CARLA_MAP);
			}
			controllerW.setSfondo(background);

			// Musica
			if (controllerW.getPlayer() == null) {
				controllerW.setPlayer(MediaPlayer.create(context, R.raw.map));
				controllerW.getPlayer().setLooping(true);
				controllerW.getPlayer().start();
			} else {
				Music.changeMusic(context, controllerW.getPlayer(), R.raw.map);
				controllerW.getPlayer().setLooping(true);
			}

			// Inizializzo dialoghi
			insultiCurrentGuybrush = dialogs.initInsulti();
			controInsultiGuybrush = dialogs.initControInsulti();

			// Inizializzo segnalini di Vittoria Round a 0
			PIRATE_VINCE = 0;
			GUYBRUSH_VINCE = 0;

			// Inizializzo sprite Guybrush inizio duello
			controllerG.setSprite(new SpriteTile(R.drawable.guybrush_talk,
					R.xml.guybrush_talk, context));
			controllerG.setAnimation("parla");

			// Carico discorsi Guybrush inizio duello
			mainThread.notifyMessage(INIT_GUYBRUSH);
			
			turnDialog = INIT_ROUND;
			break;
		}

		case INIT_MAP_POST_INTRO: {
			//Vengono caricate tutte le risorse necessarie al PRIMO caricamento della mappa durante tutto il gioco
			
				//Viene fatta pulizia delle Sprite usate nell'intro
				controllerW.getSprite(0).recycle();
				controllerW.getSprite(1).recycle();
				controllerW.getSprite(2).recycle();
				controllerW.getSprite(3).recycle();

				// Musica
				controllerW.setPlayer(Music.changeMusic(context, mPlayer, R.raw.map));
				controllerW.getPlayer().setLooping(true);

				// Inizializzo touch per cliccare i PIRATI sulla mappa
				controllerW.initMap(NO_CARLA_MAP); 
				
				// Carico la mappa iniziale
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.map1);
				controllerW.setSfondo(background);

				// Inizializzo insulti (Si inizia con 2 insulti e 2 controInsulti conosciuti)
				insultiCurrentGuybrush = dialogs.initInsulti();
				controInsultiGuybrush = dialogs.initControInsulti();

				controllerW.setPirate(WAITING_PIRATE);
				controllerW.setIntro(NO_INTRO);
				turnDialog++;
				break;
		}

		case INIT_ROUND: {
			if (controllerW.getPirate() == NO_PIRATE_SELECTED || controllerW.getPirate() == WAITING_PIRATE) {
				break;
			} else {

				if (controllerW.getPirate() == CARLA) {
					// INTRO CARLA
					if (carlaInit != CARLA_INIT_FINISHED)
						carlaIntroVideo();

					if (carlaInit != 3 && carlaInit != CARLA_INIT_FINISHED) {
						turnDialog = INIT_ROUND;
						break;
					}
					carlaInit = CARLA_INIT_FINISHED;
				} else {
					// PIRATA
					// Carico sfondo Foresta
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.landscape);
					controllerW.setSfondo(background);
				}

				switch (controllerW.getPirate()) {
				// Imposto XML e textColor
				case PIRATA_BIONDO:
					// 1 Biondo
					pirate_talk = R.drawable.pirate1_talk;
					pirate_fight = R.drawable.pirate1_fight;
					pirate_lose = R.drawable.pirate1_lose;
					controllerE.setColor(Color.YELLOW);
					break;

				case PIRATA_CAPPELLO:
					// 2 Cappello
					pirate_talk = R.drawable.pirate2_talk;
					pirate_fight = R.drawable.pirate2_fight;
					pirate_lose = R.drawable.pirate2_lose;
					controllerE.setColor(Color.MAGENTA);
					break;

				case PIRATA_BANDANA:
					// 3 Bandana
					pirate_talk = R.drawable.pirate3_talk;
					pirate_fight = R.drawable.pirate3_fight;
					pirate_lose = R.drawable.pirate3_lose;
					controllerE.setColor(Color.RED);
					break;

				case PIRATA_PELATO:
					// 4 Pelato
					pirate_talk = R.drawable.pirate4_talk;
					pirate_fight = R.drawable.pirate4_fight;
					pirate_lose = R.drawable.pirate4_lose;
					controllerE.setColor(Color.rgb(255, 165, 0)); // ORANGE
					break;

				case CARLA:
					pirate_talk = R.drawable.pirate5_talk;
					pirate_fight = R.drawable.pirate5_fight;
					pirate_lose = R.drawable.pirate5_lose;
					controllerE.setColor(Color.GREEN);
					break;

				}

				// Carico la sprite Pirata e imposto animazione iniziale
				controllerE.setSprite(new SpriteTile(pirate_talk,
						R.xml.pirate_talk, context));
				controllerE.setAnimation("pirata-parla");
				controllerG.setAnimation("parla");

				// Tolgo il lock al render dei pirati/guybrush
				controllerG.setLock(NO_LOCK);
				controllerE.setLock(NO_LOCK);

				turnDialog++;
				break;
			}
		}
		// INTRO
		case ROUND_TALK_0: { 
			// Pirata "accoglie" Guybrush
			if (controllerW.getPirate() != CARLA) {
				controllerG.setIdle();
				controllerE.getText().setString(dialogs.getIntroPirate());
				controllerE.startSpeaking(dialogs.getIntroPirate());
			} else {
				// CARLA
				
				// Carico sfondo Casa Carla
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.room044);
				controllerW.setSfondo(background);

				if (justSeenCarla == 1) {
					// Non è la prima volta che sfido Carla
					controllerE.getText().setString(dialogs.getIntroGuybrushCarla(0));
					controllerG.startSpeaking(dialogs.getIntroGuybrushCarla(0));
					controllerE.setIdle();
					
					// Musica
					Music.changeMusic(context, controllerW.getPlayer(), R.raw.swordmusic);
					controllerW.getPlayer().setLooping(true);
					turnDialog = ROUND_TALK_3;
					break;
				} else
					mainThread.notifyMessage(INIT_GUYBRUSH_CARLA); // Prima volta Carla
				
				// Metto in wait Carla
				controllerE.setIdle();
			}

			// Musica
			Music.changeMusic(context, controllerW.getPlayer(), R.raw.swordmusic);
			controllerW.getPlayer().setLooping(true);

			turnDialog++;
			break;
		}
		case ROUND_TALK_1: { // Guybrush risponde
			if (controllerE.getSpeaking() || controllerG.getSpeaking())
				// Vero se su schermo compaiono ancora le scritte
				break; // Deve aver smesso di parlare il pirata
			else {
				controllerG.stopSpeaking();
				controllerE.stopSpeaking();

				mainThread.notifyMessage(LVIEW_VISIBLE); // Rendi visibile le scelte
				controllerG.setInputWaiting(true); // Guybrush in Waiting input
				
				turnDialog++;
				break;
			}
		}
		case ROUND_TALK_2: {
			//Si avanza solo se l'utente ha scelto cosa far dire a Guybrush
			if (controllerG.getInputWaiting())
				break;
			else {
				controllerG.startSpeaking();
				turnDialog++;
				break;
			}
		}
		case ROUND_TALK_3: {
			if (controllerG.getSpeaking() || controllerE.getSpeaking())
				break; // Deve aver smesso di parlare il pirata
			else {
				controllerG.stopSpeaking();
				controllerE.stopSpeaking();

				if (justSeenCarla == 1 && controllerW.getPirate() == CARLA) {
					// Carla:"Facciamola finita con questa storia"
					controllerE.startSpeaking(dialogs.getCarlaAnswer(3));
					turnDialog++;
					break;
				}
				
				// Altrimenti il pirata risponde a tono
				int answer = guybrush.getSpeechIndex();
				if (controllerW.getPirate() != CARLA) {
					controllerE.startSpeaking(dialogs.getPirateAnswer(answer));
					switch (answer) {
					case ANSWER_INIT_ROUND:
						turnDialog++; // Inizia lo scontro
						break;
					case LEAVE_INTRO_SPEECH:
						turnDialog = RELOAD_MAP; // Abbandona il pirata
						break;
					default: 
						turnDialog = ROUND_TALK_1; // Ricomincia il discorso
						break;
					}
				} else {
					//Discorso pre-Duello Carla
					carlaIntroSpeech(answer);
					break;
				}

			}
		}
		case ROUND_TALK_4: {
			if (controllerE.getSpeaking())
				// Vero se su schermo compaionoancora le scritte
				break; // Deve aver smesso di parlare il pirata
			else {
				if (controllerW.getPirate() == CARLA)
					justSeenCarla = 1;
				
				// I pirati stanno per estrarre le spade
				controllerG.setAnimation("estrae");
				if(controllerW.getPirate() != CARLA)
					controllerE.setAnimation("pirata-estrae");
				else
					controllerE.setAnimation("carla-estrae");
				turnDialog++;
				break;
			}
		}
		case ROUND_0: { // Inizia il duello, Carico Sprite e dico alla View di
					// preparare gli insulti
			if (controllerE.isMoving() || controllerG.isMoving()) {
				break;
			} else {
				controllerG.setPoints(0);
				controllerE.setPoints(0);
				
				// Riciclo le sprite della chiacchierata iniziale
				guybrush.getSprite().recycle();
				enemy.getSprite().recycle();

				// Imposto le sprite da Combattimento
				controllerG.setSprite(new SpriteTile(
						R.drawable.guybrush_fight, R.xml.guybrush_fight,
						context));
				controllerE.setSprite(new SpriteTile(pirate_fight,
						R.xml.pirate_fight, context));

				controllerG.setAnimation("inizio-duello");
				controllerE.setAnimation("pirata-inizio-duello");
				controllerG.startMoving();
				controllerE.startMoving();
				
				soundPlayer.SuonoColpo(); // Suono spada

				// Carico gli insulti corretti del nemico
				if (controllerW.getPirate() != CARLA)
					insultiCurrentEnemy = dialogs.loadInsultEnemy();
				else {
					dialogs.loadInsultBoss(controllerW.getLevel());
					insultiCurrentEnemy = dialogs.initInsultBoss();
				}
				controllerE.setInsults(insultiCurrentEnemy);
				
				// Carico gli insulti/controInsulti di Guybrush e li imposto ai controller
				controllerG.setInsults(insultiCurrentGuybrush);
				controllerG.setControInsulti(controInsultiGuybrush);
				
			
				mainThread.notifyMessage(INIT_INSULTS); // Carica insulti anzichè dialoghi
				
				if (controllerW.getPirate() != CARLA) {
					turnDialog++;
					break;
				} else {
					turnDialog = ROUND_5_PIRATE_TURN;
					break;
				}
			}
		}
		case ROUND_1_USER_INSULTI: { // Rendo visibili gli insulti. Guybrush deve scegliere
			if (controllerE.isMoving() || controllerG.isMoving()
					|| controllerE.getSpeaking()) {
				break;
			} else {
				mainThread.notifyMessage(LVIEW_VISIBLE); // Rendi visibile le scelte
				controllerG.setInputWaiting(true); // Guybrush in Waiting
				turnDialog++;
				break;
			}
		}
		case ROUND_2_PIRATE_CHECK_ANSWER: {
			// Guybrush ha scelto e Parla. Il Pirata calcola la risposta giusta o sbagliata.
			boolean pirataOk = false;
			
			if (controllerG.getInputWaiting())
				break;
			else {
				int guybrush_risposta = insultiCurrentGuybrush.keyAt(guybrush.getSpeechIndex());

				// Sistemo animazioni
				if (guybrush.getAnimation() == "inizio-duello")
					controllerG.setAnimation("parla_stoccata");
				else if (guybrush.getAnimation() == "difesa")
					controllerG.setAnimation("parla_difesa");
				else
					controllerG.setAnimation("parla_attacco");

				// Finto insulto guybrush. Perde sicuro
				if (guybrush_risposta == MAMMA_BRUTTO || guybrush_risposta == ARRR) {
					controllerE.setPoints(enemy.getPoints() + 1);
					controllerE.setAnswerKey(PIRATE_INCREDULO);
					controllerG.startSpeaking();
					turnDialog++;
					break;
				}

				Random ran = new Random();
				if (ran.nextInt(2) == 0) { // Il pirata risponde giusto
					pirataOk = true;
					controllerE.setAnswerKey(insultiCurrentGuybrush
							.keyAt(controllerG.getSpeechIndex())); // Salva la KEY da eliminare
					controllerE.setPoints(controllerE.getPoints() + 1);

				} else {
					int lose_answer = ran.nextInt(3);
					switch (lose_answer) {
					case 0:
						controllerE.setAnswerKey(OH_YEAH); // Oh yeah?
						break;
					case 1:
						controllerE.setAnswerKey(GOMMA_COLLA); // Io sono la gomma, tu la
														// colla
						break;
					case 2:
						controllerE.setAnswerKey(TREMO); // Sto tremando sto
														// tremando
						break;

					}
					controllerG.setPoints(controllerG.getPoints() + 1);
				}

				// Inserisco l'insulto da rimuovere in un sparseArray temporaneo
				// che tiene nota degli insulti rimossi
				insultiCurrentRemoved.put(
						controllerG.getInsults().keyAt(
								controllerG.getSpeechIndex()), controllerG
								.getInsults()
								.valueAt(controllerG.getSpeechIndex()));

				// Se non conosco il controinsulto
				if (pirataOk) {
					if (dialogs.conosceControInsulto(
							controllerE.getAnswerKey(), controInsultiGuybrush) == false) {
						Log.d("IMPARATO CONTROINSULTO",
								"Chiave " + controllerE.getAnswerKey());

						dialogs.imparaControInsulto(controllerE.getAnswerKey(),
								controInsultiGuybrush); // Lo imparo
						
						mainThread.notifyMessage(LISTVIEW_UPDATE);
					}
				}

				// Rimuovo l'insulto detto dalla lista CORRENTE

				dialogs.rimuoviInsulto(guybrush.getSpeechIndex(),
						controllerG.getInsults());

				mainThread.notifyMessage(LISTVIEW_UPDATE); // Notifico alla ListView che deve
												// aggiornare la lista

				controllerG.startSpeaking();
				turnDialog++;
				break;
			}
		}
		case ROUND_3: {
			if (controllerG.getSpeaking())
				break;
			else {
				controllerG.stopSpeaking();

				int guybrush_risposta = insultiCurrentGuybrush.keyAt(controllerG
						.getSpeechIndex());

				if (guybrush_risposta == ARRENDO_INSULTI) { // Mi arrendo
					turnDialog = RELOAD_MAP;
					break;
				}

				if (controllerE.getAnimation() == "pirata-difesa")
					controllerE.setAnimation("pirata-parla-difesa");
				else if (controllerE.getAnimation() == "pirata-stoccata-alta")
					controllerE.setAnimation("pirata-parla-attacco");
				else
					controllerE.setAnimation("pirata-parla");

				// Pirata risponde
				if (controllerE.getAnswerKey() == PIRATE_INCREDULO) {
					controllerE
							.startSpeaking(context.getResources().getString(PIRATE_INCREDULO_STRING));
					turnDialog++;
					break;
				} else {
					controllerE.startSpeaking(dialogs
							.getControInsulto(controllerE.getAnswerKey()));
					turnDialog++;
					break;
				}
			}
		}
		case ROUND_4: {
			if (controllerE.getSpeaking())
				break; // Deve aver smesso di parlare il pirata
			else {
				if ((controllerE.getAnswerKey() != OH_YEAH
						&& controllerE.getAnswerKey() != GOMMA_COLLA && controllerE
						.getAnswerKey() != TREMO)
						|| controllerE.getAnswerKey() == PIRATE_INCREDULO) {
					// TOCCA AL PIRATA
					controllerG.setAnimation("difesa");
					controllerE.setAnimation("pirata-stoccata-alta");
					soundPlayer.SuonoColpo2();
					if ((controllerW.getPirate() != CARLA && controllerE.getPoints() == 3)
							|| (controllerW.getPirate() == 5 && controllerE
									.getPoints() == 5)) {
						turnDialog = ROUND_11_CHECK_WINNER;
						PIRATE_VINCE = 1;
						break;
					} else {
						turnDialog++;
						break;
					}
				} else {
					// TOCCA ANCORA A GUYBRUSH
					controllerG.setAnimation("stoccata_offensiva_bassa");
					controllerE.setAnimation("pirata-difesa");
					soundPlayer.SuonoColpo();
					if ((controllerW.getPirate() != CARLA && controllerG.getPoints() == 3)
							|| (controllerW.getPirate() == CARLA && controllerG
									.getPoints() == 5)) {
						turnDialog = ROUND_11_CHECK_WINNER;
						GUYBRUSH_VINCE = 1;
						break;
					} else {
						turnDialog = ROUND_1_USER_INSULTI;
						break;
					}
				}
			}
		}

		case ROUND_5_PIRATE_TURN: {
			if (controllerE.isMoving() || controllerG.isMoving())
				break;
			else {
				controllerE.stopSpeaking();
				
				//Pirata calcola un insulto da fare a Guybrush
				Random ran = new Random();
				int insulto_scelto_index = ran.nextInt(controllerE.getInsults()
						.size() - 3);

				controllerE.setInsultKey(insultiCurrentEnemy
						.keyAt(insulto_scelto_index));

				// Se non conosco l'insulto
				if (dialogs.conosceInsulto(
						insultiCurrentEnemy.keyAt(insulto_scelto_index),
						insultiCurrentGuybrush) == false
						&& insultiCurrentRemoved.get(insultiCurrentEnemy
								.keyAt(insulto_scelto_index)) == null
						&& controllerW.getPirate() != CARLA) {
					dialogs.imparaInsulto(
							insultiCurrentEnemy.keyAt(insulto_scelto_index),
							insultiCurrentGuybrush); // Lo imparo
					
					mainThread.notifyMessage(LISTVIEW_UPDATE);
				}

				// Setto l'anim
				if (controllerE.getAnimation().equalsIgnoreCase(
						"pirata-inizio-duello"))
					controllerE.setAnimation("pirata-parla");
				else if (controllerE.getAnimation().equalsIgnoreCase(
						"pirata-stoccata-alta"))
					controllerE.setAnimation("pirata-parla-attacco");
				else
					controllerE.setAnimation("pirata-parla-difesa");

				// Pirata risponde
				controllerE.startSpeaking(controllerE.getInsults().valueAt(
						insulto_scelto_index));
				controllerE.startMoving();

				// Il pirata non può rifare lo stesso insulto
				dialogs.rimuoviInsulto(insulto_scelto_index,
						insultiCurrentEnemy);

				turnDialog++;
				break;
			}

		}

		case ROUND_6_USER_CONTROINSULTI: {
			if (controllerE.getSpeaking())
				break;
			else {
				//L'utente deve scegliere il controinsulto
				controllerE.stopSpeaking();
				mainThread.notifyMessage(INIT_CONTROINSULTS); // Carica controInsulti
				mainThread.notifyMessage(LVIEW_VISIBLE); // Rendi visibile le scelte
				controllerG.setInputWaiting(true); // Guybrush in Waiting
				turnDialog++;
				break;
			}
		}
		case ROUND_7: {
			if (controllerG.getInputWaiting())
				break;
			else {
				// L'utente ha scelto il controinsulto
				controllerG.setInputWaiting(false);
				
				// Aggiorno animazioni
				if (controllerG.getAnimation().equalsIgnoreCase("inizio-duello")
						|| controllerG.getAnimation().equalsIgnoreCase(
								"parla_stoccata"))
					controllerG.setAnimation("parla_stoccata");
				else if (controllerG.getAnimation().equalsIgnoreCase(
						"stoccata_offensiva_alta")
						|| controllerG.getAnimation().equalsIgnoreCase(
								"parla_attacco"))
					controllerG.setAnimation("parla_attacco");
				else
					controllerG.setAnimation("parla_difesa");

				controllerG.startSpeaking();
				turnDialog++;
				break;
			}
		}

		case ROUND_8_CHECK_CONTROINSULTO: {
			if (controllerG.getSpeaking())
				break;
			else {
				controllerG.stopSpeaking();

				if (controInsultiGuybrush.keyAt(guybrush.getSpeechIndex()) == ARRENDO_CONTROINSULTI) {
					turnDialog = RELOAD_MAP;
					break;
				}

				if (controInsultiGuybrush.keyAt(guybrush.getSpeechIndex()) == RIPETERE) { // Ripetere
					// Aggiorno animazioni parlano
					if (controllerE.getAnimation().equalsIgnoreCase(
							"pirata-inizio-duello")
							|| controllerE.getAnimation().equalsIgnoreCase(
									"pirata-parla"))
						controllerE.setAnimation("pirata-parla");
					else if (controllerE.getAnimation().equalsIgnoreCase(
							"pirata-stoccata")
							|| controllerE.getAnimation().equalsIgnoreCase(
									"pirata-parla-attacco"))
						controllerE.setAnimation("pirata-parla-attacco");
					else
						controllerE.setAnimation("pirata-parla-difesa");

					// Pirata ripete
					controllerE.startSpeaking(enemy.getSpeech());
					controllerE.startMoving();
					turnDialog = ROUND_6_USER_CONTROINSULTI;
					break;
				}

				if (controInsultiGuybrush.keyAt(controllerG.getSpeechIndex()) == enemy
						.getInsultKey()) {
					
					// RISPOSTA GIUSTA!
					Log.d(TAG, "RISPOSTA GIUSTA");
					
					controllerG.setPoints(controllerG.getPoints() + 1);
					
					if (controllerG.getPoints() == controllerE.getPoints()) {
						controllerG.setAnimation("inizio-duello");
						controllerE.setAnimation("pirata-inizio-duello");
						
						soundPlayer.SuonoColpo();
	
					} else {
						controllerG.setAnimation("stoccata_offensiva_alta");
						controllerE.setAnimation("pirata-difesa");
						
						soundPlayer.SuonoColpo2();
					}
					controllerG.startMoving();
					controllerE.startMoving();

					turnDialog++;
					break;

				} else {
					
					// RISPOSTA SBAGLIATA
					Log.d(TAG, "RISPOSTA SBAGLIATA");
					
					controllerE.setPoints(controllerE.getPoints() + 1);
					controllerG.setAnimation("difesa");
					controllerE.setAnimation("pirata-stoccata-alta");
					
					soundPlayer.SuonoColpo2();
					
					controllerG.startMoving();
					controllerE.startMoving();
					turnDialog = ROUND_10;
					break;
				}

			}
		}

		case ROUND_9: {
			if (controllerE.isMoving() || controllerG.isMoving())
				break;
			else {
				// Check vittoria Round
				if ((controllerW.getPirate() != CARLA && controllerG.getPoints() == 3)
						|| (controllerW.getPirate() == CARLA && controllerG.getPoints() == 5)) {
					GUYBRUSH_VINCE = 1;
					turnDialog = ROUND_11_CHECK_WINNER;
					break;
				} else {
					// Tocca a Carla fare l'insulto anche se ha risposto giusto Guybrush
					if (controllerW.getPirate() == CARLA) {
						turnDialog = ROUND_5_PIRATE_TURN;
						break;
					} else {
						// Tocca a Guybrush fare l'insulto
						mainThread.notifyMessage(INIT_INSULTS); // Carico gli INSULTI
						turnDialog = ROUND_1_USER_INSULTI;
						break;
					}
				}
			}
		}

		case ROUND_10: {
			if (controllerE.isMoving() || controllerG.isMoving())
				break;
			else {
				Log.d(TAG, "Punti Enemy " + controllerE.getPoints());
				if ((controllerW.getPirate() != CARLA && controllerE.getPoints() == 3)
						|| (controllerW.getPirate() == CARLA && controllerE.getPoints() == 5)) {
					PIRATE_VINCE = 1;
					turnDialog = ROUND_11_CHECK_WINNER;
					break;
				} else {
					// Tocca ancora al pirata
					turnDialog = ROUND_5_PIRATE_TURN;
					break;
				}
			}
		}

		case ROUND_11_CHECK_WINNER: { // Chi ha vinto?
			if (controllerE.isMoving() || controllerG.isMoving())
				break;
			else {
				if (PIRATE_VINCE == 1) {
					//Ha vinto il Pirata!
					controllerG.getSprite().recycle();
					
					// Carico sprite Guybrush perde
					controllerG.setSprite(new SpriteTile(
							R.drawable.guybrush_lose, R.xml.guybrush_lose,
							context));
					controllerG.setAnimation("perde");
					controllerE.setAnimation("pirata-stoccata-alta");
					
					soundPlayer.SuonoGuybrushPerde();
					
					controllerG.startMoving();
				} else if (GUYBRUSH_VINCE == 1) {
					//Ha vinto Guybrush
					controllerE.getSprite().recycle();
					
					// Carico sprite Pirata perde
					controllerE.setSprite(new SpriteTile(pirate_lose,
							R.xml.pirate_lose, context));
					
					controllerE.setAnimation("pirata_perde");
					
					soundPlayer.SuonoPirataPerde();
					
					controllerE.startMoving();
				}
				turnDialog++;
				break;
			}
		}
		case ROUND_12_CHECK_WINNER: {
			if (controllerG.isMoving() || controllerE.isMoving())
				break;
			else {
				if (PIRATE_VINCE == 1) {
					controllerG.setAnimation("perde_gira");
					controllerG.startMoving();
					controllerE.setAnimation("pirata-parla-attacco");
					if (controllerW.getPirate() != CARLA) {
						controllerE.startSpeaking(context.getResources().getString(PIRATA_VINTO));
						turnDialog++;
						break;
					} else {
						controllerE.startSpeaking(context.getResources().getString(CARLA_VINCE));
						turnDialog = RELOAD_MAP;
						break;
					}
				} else if (GUYBRUSH_VINCE == 1) {
					if (controllerW.getPirate() == CARLA) {
						controllerE.setAnimation("pirata_perde_parla");
						turnDialog++;
						break;
					}
					// Check Vittorie totali
					controllerG.setVittorie(controllerG.getVittorie() + 1);
				
					controllerE.setAnimation("pirata_perde_parla");
					
					if(controllerG.getVittorie() >= MINIMO_VITTORIE_READY_CARLA && 
							controllerG.getXpPerc(controllerW.getLevel(), insultiCurrentGuybrush) > PERC_MIN_INSULTI_READY_CARLA){
						controllerE.startSpeaking(context.getResources().getString(READY_CARLA));
						controllerW.setOkCarla(SI_CARLA_MAP);
					}
					else
						controllerE.startSpeaking(context.getResources().getString(PIRATA_PERDE));			
				}
				turnDialog++;
				break;
			}
		}
		case ROUND_13_OUTRO_MATCH: {
			if (controllerG.isMoving() || controllerE.getSpeaking())
				break;
			else {
				//Guybrush ha vinto e inzia discorso finale con Carla
				if (controllerW.getPirate() == CARLA) {
					carlaOutro();
					break;
				}
				if (PIRATE_VINCE == 1) {
					controllerE.stopSpeaking();
					controllerG.setAnimation("perde_parla");
					controllerG.getText().setString(context.getResources().getString(GUYBRUSH_SCAPPA));
					controllerG.startSpeaking();
					turnDialog++;
					break;
				} else {
					// Guybrush ha vinto, e si ricarica la mappa
					turnDialog++;
					break;
				}
			}
		}
		case RELOAD_MAP: {
			if (controllerG.getSpeaking() || controllerE.getSpeaking())
				break;
			else {
				controllerG.stopSpeaking();
				controllerE.stopSpeaking();

				controllerE.setType(NO_TYPE_SELECTED);
				controllerW.setPirate(WAITING_PIRATE);

				controllerG.setLock(SI_LOCK);
				controllerE.setLock(SI_LOCK);

				background.recycle();
				controllerG.getSprite().recycle();
				controllerW.setSfondo(null);

				turnDialog = INIT_MAP;
				break;
			}
		}
		case OUTRO_FUOCO: {
			outroFuoco();
			break;
			// FINE GIOCO
		}

		}
	}

	private void outroFuoco() {
		if (controllerW.getSpeaking(GUYBRUSH))
			turnDialog = OUTRO_FUOCO;
		else {
			switch (turnOutro) {
			case 0:
				controllerW.setIntro(FUOCO_FINE);
				
				// Musica
				if (controllerW.getPlayer() == null) {
					controllerW.setPlayer(MediaPlayer.create(context, R.raw.end));
					controllerW.getPlayer().setLooping(false);
					controllerW.getPlayer().start();
				} else {
					Music.changeMusic(context, controllerW.getPlayer(), R.raw.end);
					controllerW.getPlayer().setLooping(false);
				}
				
				
				if(guybrushTextIntro == null){ // Check Standby
					// TESTI
					guybrushTextIntro = new Text(context);
					guybrushTextIntro.setString("");
					guybrushTextIntro.setColor(Color.WHITE);
					guybrushTextIntro.setTimeShow(SCABB_FUOCO_TIME);
					controllerW.setText(guybrushTextIntro, 1);
				}

				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.fuoco);
				controllerW.setSfondo(background);

				// GUYBRUSH SPRITE 1
				SpriteTile guybrush_fuoco = new SpriteTile(
						R.drawable.guybrush_fuoco, R.xml.guybrush_fuoco,
						context);
				guybrush_fuoco.setCurrentAnimation("guybrush_fuoco", true);
				controllerW.setSprite(guybrush_fuoco, 1);

				// FUOCONE SPRITE 4
				SpriteTile fuocone = new SpriteTile(R.drawable.fuocone,
						R.xml.fuocone, context);
				fuocone.setCurrentAnimation("fuocone", true);
				controllerW.setSprite(fuocone, 4);
			case 1:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 2:
				controllerW.getText(GUYBRUSH).setTimeShow(5000);
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 3:
				controllerW.getText(GUYBRUSH).setTimeShow(2000);
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 4:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.setSpeaking(GUYBRUSH, true);
				controllerW.setSpeech(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				controllerW.getText(GUYBRUSH).setString(
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 5:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 6:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.setSpeaking(GUYBRUSH, true);
				controllerW.setSpeech(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				controllerW.getText(GUYBRUSH).setString(
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 7:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 8:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.setSpeaking(GUYBRUSH, true);
				controllerW.setSpeech(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				controllerW.getText(GUYBRUSH).setString(
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 9:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 10:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.setSpeaking(GUYBRUSH, true);
				controllerW.setSpeech(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				controllerW.getText(GUYBRUSH).setString(
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				turnOutro++;
				break;
			case 11:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnOutro + 36));
				controllerW.getText(GUYBRUSH).setTimeShow(2000);
				turnOutro++;
				break;
			case 12:
				// TITOLI DI CODA
				controllerW.getSprite(4).recycle();
				controllerW.getSprite(1).recycle();

				// Musica
				Music.changeMusic(context, controllerW.getPlayer(), R.raw.creditslechuck);
				
				controllerW.setIntro(CREDITS);

				creditsTime = System.currentTimeMillis();

				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.credits1);
				controllerW.setSfondo(background);
				turnOutro++;
				break;

			case 13:
				if (System.currentTimeMillis() - creditsTime < 5000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits2);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 14:
				if (System.currentTimeMillis() - creditsTime < 10000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits3);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 15:
				if (System.currentTimeMillis() - creditsTime < 15000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits4);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 16:
				if (System.currentTimeMillis() - creditsTime < 20000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits5);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 17:
				if (System.currentTimeMillis() - creditsTime < 25000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits6);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 18:
				if (System.currentTimeMillis() - creditsTime < 30000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits7);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 19:
				if (System.currentTimeMillis() - creditsTime < 35000)
					break;
				else {
					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.credits8);
					controllerW.setSfondo(background);
					turnOutro++;
					break;
				}
			case 20:
				if (System.currentTimeMillis() - creditsTime < 119000)
					break;
				else {
					controllerW.setEnd(END_GAME);
					Intent intent = new Intent(Intent.ACTION_MAIN);
					intent.addCategory(Intent.CATEGORY_HOME);
					intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
					context.startActivity(intent);
					break;
				}
			}

		}
	}

	private void introFuoco() {
		if (controllerW.getSpeaking(GUYBRUSH) || controllerW.getSpeaking(PIRATA_SX)
				|| controllerW.getSpeaking(PIRATA_DX)) {
			turnDialog = SCABB_FUOCO;
			return;
		} else {
			if (controllerW.getSkipped() == 1)
				turnIntro = 40;
			switch (turnIntro) {
			case 0:
				controllerW.initSkip();
			case 1:
			case 2:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnIntro));
				turnIntro++;
				break;
			case 3:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.getSprite(PIRATA_DX)
						.setCurrentAnimation("pirate2_sx", true);
				controllerW.getSprite(PIRATA_DX).setAnimationIdle();
				controllerW.startSpeaking(PIRATA_SX, dialogs.getIntroPirate1Fuoco(0));
				turnIntro++;
				break;
			case 4:
				controllerW.startSpeaking(PIRATA_SX, dialogs.getIntroPirate1Fuoco(1));
				turnIntro++;
				break;
			case 5:
				controllerW.stopSpeaking(PIRATA_SX);
				controllerW.getSprite(PIRATA_DX)
						.setCurrentAnimation("pirate2_dx", true);
				controllerW.getSprite(PIRATA_DX).setAnimationIdle();
				controllerW.startSpeaking(PIRATA_DX, dialogs.getIntroPirate2Fuoco(0));
				turnIntro++;
				break;
			case 6:
			case 7:
			case 8:
				controllerW.stopSpeaking(PIRATA_DX);
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnIntro - 3));
				turnIntro++;
				break;
			case 9:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.startSpeaking(PIRATA_SX, dialogs.getIntroPirate1Fuoco(2));
				controllerW.getSprite(PIRATA_DX)
						.setCurrentAnimation("pirate2_sx", true);
				controllerW.getSprite(PIRATA_DX).setAnimationIdle();
				controllerW.startSpeaking(PIRATA_DX, dialogs.getIntroPirate2Fuoco(1));
				turnIntro++;
				break;
			case 10:
				controllerW.stopSpeaking(PIRATA_SX);
				controllerW.startSpeaking(PIRATA_DX, dialogs.getIntroPirate2Fuoco(2));
				turnIntro++;
				break;
			case 11:
				controllerW.getSprite(PIRATA_DX)
						.setCurrentAnimation("pirate2_dx", true);
				controllerW.getSprite(PIRATA_DX).setAnimationIdle();
			case 12:
			case 13:
			case 14:
			case 15:
			case 16:
			case 17:
			case 18:
			case 19:
			case 20:
			case 21:
			case 22:
				controllerW.stopSpeaking(PIRATA_DX);
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnIntro - 5));
				turnIntro++;
				break;
			case 23:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.getSprite(PIRATA_DX)
						.setCurrentAnimation("pirate2_sx", true);
				controllerW.getSprite(PIRATA_DX).setAnimationIdle();
				controllerW.startSpeaking(PIRATA_SX, dialogs.getIntroPirate1Fuoco(3));
				controllerW.startSpeaking(PIRATA_DX, dialogs.getIntroPirate2Fuoco(3));
				turnIntro++;
				break;
			case 24:
				controllerW.getSprite(PIRATA_DX)
						.setCurrentAnimation("pirate2_dx", true);
				controllerW.getSprite(PIRATA_DX).setAnimationIdle();
				controllerW.startSpeaking(PIRATA_SX, dialogs.getIntroPirate1Fuoco(4));
				controllerW.startSpeaking(PIRATA_DX, dialogs.getIntroPirate2Fuoco(4));
				turnIntro++;
				break;
			case 25:
			case 26:
			case 27:
			case 28:
			case 29:
			case 30:
			case 31:
			case 32:
				controllerW.stopSpeaking(PIRATA_SX);
				controllerW.stopSpeaking(PIRATA_DX);
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnIntro - 7));
				turnIntro++;
				break;
			case 33:
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.startSpeaking(PIRATA_SX, dialogs.getIntroPirate1Fuoco(5));
				controllerW.startSpeaking(PIRATA_DX, dialogs.getIntroPirate2Fuoco(5));
				turnIntro++;
				break;

			case 34:
			case 35:
			case 36:
			case 37:
			case 38:
			case 39:
				controllerW.stopSpeaking(PIRATA_SX);
				controllerW.stopSpeaking(PIRATA_DX);
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnIntro - 8));
				turnIntro++;
				break;
			case 40:
				// Scelta Livelli
				controllerW.setSkipped(NO_SKIPPABLE);
				controllerW.stopSpeaking(GUYBRUSH);
				controllerW.stopSpeaking(PIRATA_SX);
				controllerW.stopSpeaking(PIRATA_DX);
				controllerW.setIntro(-1);
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.levels);
				controllerW.setSfondo(background);

				controllerW.initLevel();

				turnIntro++;
				break;
			case 41:
				if (controllerW.getLevel() == NO_LEVEL_SELECTED)
					break;
				else {
					// Inizializzo Insulti/Controinsulti giusti
					dialogs.loadInitGuybrushControInsulti(controllerW.getLevel());
					dialogs.loadInitGuybrushInsulti(controllerW.getLevel());

					background = BitmapFactory.decodeResource(
							context.getResources(), R.drawable.fuoco);
					controllerW.setSfondo(background);
					controllerW.setIntro(FUOCO_INIZIO);
					turnIntro++;
					break;
				}
			case 42:
			case 43:
			case 44:
			case 45:
				controllerW.startSpeaking(GUYBRUSH,
						dialogs.getIntroGuybrushFuoco(turnIntro - 10));
				turnIntro++;
				break;
			case 46:
				turnDialog = INIT_MAP_POST_INTRO;
				break;
			}

		}

	}

	private void carlaIntroSpeech(int answer) {

		if (controllerE.getSpeaking() || controllerG.getSpeaking()) {
			turnDialog = ROUND_TALK_3;
			return;
		}

		switch (carlaSpeechIndex) {
		case 0:
		case 1:
		case 2: {
			switch (answer) {
			case 0:
			case 1:
				controllerE.startSpeaking(dialogs.getCarlaAnswer0(answer));
				carlaSpeechIndex = 5;
				break;
			case 2:
				controllerE.startSpeaking(dialogs
						.getCarlaAnswer1(carlaSpeechIndex));
				carlaSpeechIndex++;
				break;
			}
			break;
		}
		case 3:
			controllerG.startSpeaking(dialogs.getIntroGuybrushCarla(1));
			carlaSpeechIndex++;
			break;
		case 4:
		case 5:
		case 6:
		case 7:
			controllerE.startSpeaking(dialogs
					.getCarlaAnswer(carlaSpeechIndex - 4));
			if (carlaSpeechIndex == 7)
				turnDialog++;
			carlaSpeechIndex++;
			break;
		}
	}

	private void carlaOutro() {

		if (controllerE.getSpeaking() || controllerG.getSpeaking()) {
			return;
		}

		switch (carlaSpeechIndexOutro) {
		case 0:
		case 1:
		case 2:
		case 3:
			controllerE.startSpeaking(dialogs
					.getCarlaLose(carlaSpeechIndexOutro));
			carlaSpeechIndexOutro++;
			break;
		case 4:
			controllerE.setLock(SI_LOCK);
			controllerG.setLock(SI_LOCK);
			guybrushOutroTime = System.currentTimeMillis();
			background = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.guybrushwin0);
			controllerW.setSfondo(background);
			carlaSpeechIndexOutro++;
			break;
		case 5:
			if (System.currentTimeMillis() - guybrushOutroTime < 4000)
				break;
			else {
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.guybrushwin1);
				controllerW.setSfondo(background);
				carlaSpeechIndexOutro++;
			}
		case 6:
			if (System.currentTimeMillis() - guybrushOutroTime < 8000)
				break;
			else {
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.guybrushwin2);
				controllerW.setSfondo(background);
				carlaSpeechIndexOutro++;
			}
		case 7:
			if (System.currentTimeMillis() - guybrushOutroTime < 12000)
				break;
			else {
				turnDialog = OUTRO_FUOCO;
				break;
			}
		}
	}

	private void carlaIntroVideo() {
		switch (carlaInit) {
		case 0:
			mainThread.notifyMessage(MUSIC_PAUSE);
			carlaIntroTime = System.currentTimeMillis();
			background = BitmapFactory.decodeResource(context.getResources(),
					R.drawable.carlaintro0);
			controllerW.setSfondo(background);
			carlaInit++;
			break;
		case 1:
			if (System.currentTimeMillis() - carlaIntroTime < 4000)
				break;
			else {
				background = BitmapFactory.decodeResource(
						context.getResources(), R.drawable.carlaintro1);
				controllerW.setSfondo(background);
				carlaInit++;
				break;
			}
		case 2:
			if (System.currentTimeMillis() - carlaIntroTime < 8000)
				break;
			else {
				carlaInit++;
				break;
			}
		}

	}

	public int getTurnDialog() {
		return this.turnDialog;
	}
	public int getSeenCarla(){
		return this.justSeenCarla;
	}
	public int getCarlaInit(){
		return this.carlaInit;
	}
	
	
	public void setCarlaInit(int carlaInit){
		this.carlaInit = carlaInit;
	}
	public void setTurn(int saved) {
		this.turnDialog = saved;
	}
	public void setSeenCarla(int justSeen){
		this.justSeenCarla = justSeen;
	}
	
}