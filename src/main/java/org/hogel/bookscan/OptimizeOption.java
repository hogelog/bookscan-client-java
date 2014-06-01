package org.hogel.bookscan;

import java.util.ArrayList;
import java.util.List;

public class OptimizeOption {
    public static final String OPTIMIZE_TYPE_NAME = "optimize_type[]";

    public static enum Type {
        IPAD("ipad", "iPadチューニングβ"),
        IPAD3("ipad3", "New iPad/iPad Air/iPad mini Retina Displayチューニング1.0β"),
        IPHONE4("iphone4", "iPhone/iPod touchチューニング 1.0β"),
        IPHONE5("iphone5", "iPhone5チューニング 1.0β"),
        KINDLEP("kindlep", "Kindle Paperwhite 1.0β"),
        KINDLE4("kindle4", "Kindle4 1.0β"),
        KINDLE4T("kindle4t", "Kindle touch 1.0β"),
        KINDLE3("kindle3", "Kindle Keyboard チューニング1.0β"),
        KINDLEDX("kindledx", "Kindle DX(4th edition)チューニング1.0β"),
        KINDLEFH("kindlefh", "Kindle Fire HD チューニング1.0β"),
        ANDROID("android", "Androidチューニングβ"),
        GTAB("gtab", "Androidタブレットチューニング 1.0β"),
        ANDROIDT2("androidt2", "Androidタブレットチューニング (高解像度版)1.0β"),
        SONYREADER("sonyreader", "SONY Readerチューニング1.1β"),
        KOBO("kobo", "koboチューニング1.0β"),
        KOBOGLO("koboglo", "kobo gloチューニング1.0β"),
        NOOK("nook", "nookチューニング1.0β"),
        NOOKC("nookc", "nook colorチューニング1.0β"),
        BIBLIO("biblio", "biblioチューニング1.0β"),
        JPG("jpg", "JPEG変換(ZIP圧縮)1.0β"),
        BR("bR", "bREADERチューニング0.1α(縦書き専用)"),
        AUDIO("audio", "音声変換(2倍速)0.1α(文庫・新書対象/視覚障害者向け)"),
        ;
        private final String value;
        private final String description;

        Type(String value, String description) {
            this.value = value;
            this.description = description;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    public static enum Flag {
        COVER("cover_flg", "1", "PDFの1ページ目にサムネイル用の表紙を付ける"),
        BOLD("bold_flg", "1", "文字を太くする"),
        WHITE("white_flg", "1", "黄ばみ除去を行う"),
        DROPBOX("dropbox_flg", "1", "チューニングが完了したら同時にDropboxへもアップロードする"),
        KINDLE("kindle_flg", "1", "チューニングが完了したら同時にKindleで自動ダウンロードしておく")
        ;
        private final String inputName;
        private final String value;
        private final String description;

        Flag(String inputName, String value, String description) {
            this.inputName = inputName;
            this.value = value;
            this.description = description;
        }

        public String getInputName() {
            return inputName;
        }

        public String getValue() {
            return value;
        }

        public String getDescription() {
            return description;
        }
    }

    private final List<Type> types;
    private final List<Flag> flags;

    public OptimizeOption() {
        types = new ArrayList<>();
        flags = new ArrayList<>();
    }

    public void addType(Type type) {
        types.add(type);
    }

    public void addFlag(Flag flag) {
        flags.add(flag);
    }

    public List<Type> getTypes() {
        return types;
    }

    public List<Flag> getFlags() {
        return flags;
    }

}
