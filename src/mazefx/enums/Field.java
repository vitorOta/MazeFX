/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazefx.enums;

import javafx.scene.paint.Color;

/**
 *
 * @author VitorOta
 */
public enum Field {
    PLAYER(Color.BROWN,2147483646),
    EMPTY(Color.gray(0.8),0),
    GENERATED(Color.AQUAMARINE,2147483645),
    WALL(Color.gray(0.2),2147483647);
    
    public Color color;
    public int value;

    private Field(Color color, int value) {
        this.color = color;
        this.value=value;
    }
    
    
}
