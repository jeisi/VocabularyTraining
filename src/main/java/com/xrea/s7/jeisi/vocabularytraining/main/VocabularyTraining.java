package com.xrea.s7.jeisi.vocabularytraining.main;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.NoSuchElementException;
import java.util.jar.JarFile;

import javax.swing.JOptionPane;

import org.yaml.snakeyaml.Yaml;

import com.xrea.s7.jeisi.jlib.confirmdialog.ConfirmDialog;
import com.xrea.s7.jeisi.jlib.errordialog.ErrorDialog;
import com.xrea.s7.jeisi.vocabularytraining.phraseframe.ListeningPhraseFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.phraseframe.PhraseFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.selectcategory.SelectCategoryDialog;
import com.xrea.s7.jeisi.vocabularytraining.sentenceframe.SentenceFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.ListeningSentenceFrameBuilder;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrame;
import com.xrea.s7.jeisi.vocabularytraining.wordframe.WordFrameBuilder;

public class VocabularyTraining {

	public static void main(String[] args) {
		/*
		java.util.Properties hash=System.getProperties();
        java.util.Enumeration enu=hash.keys();
        while(enu.hasMoreElements()){
            Object key=enu.nextElement();
            System.out.println(key+"\t\t"+System.getProperty((String)key));
        }
		*/
		
		VocabularyTraining obj = new VocabularyTraining();
		VocabularyTraining.setDataDirs(new String[] {".", "../VocabularyTraining-Data/target"});
		VocabularyTraining.setUserDir(System.getProperty("user.home") + "/AppData/Local/" + getApplicationTitle());
		obj.open();
	}

	public VocabularyTraining() {
	}
	
	public void open() {
		try {
			loadConfig();
		} catch (IOException e1) {
			new ErrorDialog(null, e1, "config.yaml のロードに失敗しました。").open();
		}
		
		boolean isContinue = true;
		do {
			isContinue = onIdle();
		} while(isContinue);
	
		saveConfig();
		closeResource();
		
		System.exit(0);
	}
	
	public boolean onIdle() {
		switch(currentState) {
		case OPEN_RESOURCE:
			openResource();
			break;
		case SELECT_CATEGORY:
			execSelectCategory();
			break;
		case PHRASE:
		case SENTENCE:
		case LISTENING_PHRASE:
		case LISTENING_SENTENCE:
			execWordFrame();
			break;
		case EXIT:
			return false;
		}
		return true;
	}
	
	public static void setDataDirs(String[] dataDir_) {
		dataDirs = dataDir_;
	}
	
//	public static String getDataDir() {
//		return dataDir;
//	}
	
	public static void setUserDir(String userDir_) {
		userDir = userDir_;
		
		File file = new File(userDir);
		if(!file.exists()) {
			file.mkdirs();
		}
	}
	
	public static String getUserDir() {
		return userDir;
	}
	
	public JarFile getResourceJar() {
		return m_resourceJar;
	}
	
	public Config getConfig() {
		return m_config;
	}
	
	public static String getApplicationTitle() {
		return "VocabularyTraining";
	}
	
	private void openResource() {
		String[] dataDirs = VocabularyTraining.dataDirs;
		if(dataDirs == null) {
			throw new AssertionError("dataDirs がセットされていません。");
		}
		
		String jarName = "VocabularyTraining-Data-0.0.1-SNAPSHOT.jar";
		for(String dataDir : dataDirs) {
			File file = new File(dataDir, jarName);
			if(!file.exists()) {
				continue;
			}
			
			try {
				m_resourceJar = new JarFile(file);
			} catch (SecurityException e) {
				new ErrorDialog(null, e, "jar ファイルへのアクセスが SecurityManager によって拒否されました: " + file).open();
				changeState(STATE.EXIT);
				return;
			} catch (IOException e) {
				new ErrorDialog(null, e, "jar ファイルの読込時にエラーが発生しました: " + file).open();
				changeState(STATE.EXIT);
				return;
			}
		}
		
		if(m_resourceJar == null) {
			ConfirmDialog dialog = new ConfirmDialog(null, JOptionPane.ERROR_MESSAGE, JOptionPane.CLOSED_OPTION);
			StringBuilder builder = new StringBuilder();
			builder.append("jar ファイル '");
			builder.append(jarName);
			builder.append("' が以下のパス上に見つかりませんでした：\n");
			for(String dataDir : dataDirs) {
				builder.append("\t");
				builder.append(dataDir);
				builder.append("\n");
			}
			dialog.setMessage(builder.toString());
			dialog.open();
			changeState(STATE.EXIT);
			return;
		}
		
		changeState(STATE.SELECT_CATEGORY);
	}
	
	private void closeResource() {
		if(m_resourceJar == null) {
			return;
		}
		
		try {
			m_resourceJar.close();
			m_resourceJar = null;
		} catch (IOException e) {
			new ErrorDialog(null, e, "jar ファイルのクローズに失敗しました: " + m_resourceJar.getName()).open();
			return;
		}
	}
	
	private void execSelectCategory() {
		SelectCategoryDialog dialog = new SelectCategoryDialog(this);
		dialog.open();
		while(dialog.isDisplayable());
		
		String actionCommand = dialog.getActionCommand();
		switch(actionCommand) {
		case "phrase":
			changeState(STATE.PHRASE);
			break;
		case "sentence":
			changeState(STATE.SENTENCE);
			break;
		case "listening phrase":
			changeState(STATE.LISTENING_PHRASE);
			break;
		case "listening sentence":
			changeState(STATE.LISTENING_SENTENCE);
			break;
		case "no select":
			changeState(STATE.EXIT);
			break;
		default:
			throw new NoSuchElementException(actionCommand + "はサポート外のアクションコマンドです。");
		}
	}
	
	private void execWordFrame() {
		switch(stateHandler) {
		case 0:
			WordFrameBuilder frameBuilder = createWordFrameBuilder(currentState);
			wordFrame = frameBuilder.build(this);
			wordFrame.open();
			++stateHandler;
			// fall through //
		case 1:
			if(!wordFrame.isDisplayable()) {
				wordFrame = null;
				changeState(STATE.SELECT_CATEGORY);
			}
			break;
		default:
			throw new NoSuchElementException(stateHandler + "は範囲外の値です");
		}
	}
	
	private WordFrameBuilder createWordFrameBuilder(STATE state) {
		switch(state) {
		case PHRASE:
			return new PhraseFrameBuilder();
		case SENTENCE:
			return new SentenceFrameBuilder();
		case LISTENING_PHRASE:
			return new ListeningPhraseFrameBuilder();
		case LISTENING_SENTENCE:
			return new ListeningSentenceFrameBuilder();
		default:
			throw new NoSuchElementException(state + "は規定外の値です。");
		}
	}

	private void changeState(STATE nextState) {
		currentState = nextState;
		stateHandler = 0;
	}
	
	private void saveConfig() {
		File filename = getConfigFile();
		Yaml yaml = new Yaml();
		try(FileWriter writer = new FileWriter(filename)) {
			yaml.dump(m_config, writer);
		} catch (IOException e) {
			new ErrorDialog(null, e, "config.yaml のセーブに失敗しました。").open();
		}
	}
	
	private void loadConfig() throws IOException {
		File filename = getConfigFile();
		Yaml yaml = new Yaml();
		try(FileReader reader = new FileReader(filename)) {
			m_config = (Config) yaml.load(reader);
		} catch (FileNotFoundException e) {
			m_config = new Config();
		}
	}
	
	private File getConfigFile() {
		String dataDir = VocabularyTraining.getUserDir();
		return new File(dataDir, "config.yaml");
	}
	
	private enum STATE {
		OPEN_RESOURCE,
		SELECT_CATEGORY,
		PHRASE,
		SENTENCE,
		LISTENING_PHRASE,
		LISTENING_SENTENCE,
		EXIT,
	};
	
	private WordFrame wordFrame;
//	private WordFrameModel model;
	private static String[] dataDirs = null;
	private static String userDir = null;
	private JarFile m_resourceJar;
	private Config m_config;
	private STATE currentState = STATE.OPEN_RESOURCE;
	private int stateHandler;
}

/*
 * memo:
 * 
 * 問題データの統合はできないようにする
 *  - 基本的にはデータを選んでから、そのデータの中から出題を行う。
 *  - 基本的には複数のデータに跨っての出題は行えない。
 *  - ローカルで、問題へのリンクを集めたリンク集を作ることができる。リンク集は複数のデータを跨ることができる。
 *  - リンク集から出題を行うことができる。そうすれば、複数のデータに跨っての出題も可。
 *  - !リンク集はあくまでもローカルだけのものであり、外には出せないようにする!
 * 
 * 
 * 問題点
 *  - プログラムは共通で各社有料データ提供という形になるので、安くしないとユーザに売れないということでディスカウント合戦になる可能性がある。
 *     -> ジャンルを絞った廉価版と、色々なジャンルが入った高価版とを用意するといいのでは？
 * 
 * 
 */
