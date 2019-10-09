package me.davidnery.meusuap.utils;

import android.content.Context;
import android.content.SharedPreferences;

import me.davidnery.meusuap.R;

/**
 * Created by david on 02/11/2018.
 */

public class ConfiguracoesUtils {

    private SharedPreferences preferences;

    public ConfiguracoesUtils(Context context) {
        this.preferences = context.getSharedPreferences(context.getString(R.string.configuracoes), Context.MODE_PRIVATE);
    }

    private void setPreferences(String key, Object value) {
        SharedPreferences.Editor editor = preferences.edit();
        if (value instanceof Integer)
            editor.putInt(key, (int) value);
        else if (value instanceof Boolean)
            editor.putBoolean(key, (boolean) value);
        editor.apply();
    }

    public boolean hasKey(String key) {
        return preferences.contains(key);
    }

    public void setAnualPeso1(int peso) {
        setPreferences("anualpeso1", peso);
    }

    public int getAnualPeso1() {
        return preferences.getInt("anualpeso1", 0);
    }

    public void setAnualPeso2(int peso) {
        setPreferences("anualpeso2", peso);
    }

    public int getAnualPeso2() {
        return preferences.getInt("anualpeso2", 0);
    }

    public void setAnualPeso3(int peso) {
        setPreferences("anualpeso3", peso);
    }

    public int getAnualPeso3() {
        return preferences.getInt("anualpeso3", 0);
    }

    public void setAnualPeso4(int peso) {
        setPreferences("anualpeso4", peso);
    }

    public int getAnualPeso4() {
        return preferences.getInt("anualpeso4", 0);
    }

    public void setSemPeso1(int peso) {
        setPreferences("sempeso1", peso);
    }

    public int getSemPeso1() {
        return preferences.getInt("sempeso1", 0);
    }

    public void setSemPeso2(int peso) {
        setPreferences("sempeso2", peso);
    }

    public int getSemPeso2() {
        return preferences.getInt("sempeso2", 0);
    }

    public void setVNN(boolean vnn) {
        setPreferences("vnn", vnn);
    }

    public boolean getVNN() {
        return preferences.getBoolean("vnn", true);
    }

}
