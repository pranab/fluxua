/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package org.fluxua.driver;

import java.util.Iterator;

/**
 *
 * @author pranab
 */
public class DefaultFlowIterator implements Iterator {
    private int count = 0;

    @Override
    public boolean hasNext() {
        boolean has = count == 0;
        ++count;
        return has;
    }

    @Override
    public Object next() {
        return null;
    }

    @Override
    public void remove() {
    }

}
