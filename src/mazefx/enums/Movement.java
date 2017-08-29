/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package mazefx.enums;

/**
 *
 * @author VitorOta
 */
public enum Movement {
    RIGHT(0),
    DOWN(1),
    LEFT(2),
    UP(3);

    int index;

    private Movement(int index) {
        this.index = index;
    }
    
    public static Movement fromIndex(int nextInt) {
        return values()[nextInt];
    }
    
    public Movement getOpposite(){
        return fromIndex(index + (index > 1 ? -2 : 2));
    }
    
}
