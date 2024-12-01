package oopants.window;

import oopants.ants.Ant;
import oopants.ants.BlueAnt;
import oopants.ants.RedAnt;
import oopants.grid.Grid;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.HashSet;

public class TileCanvas extends JPanel {
    /**
     * this is the panel for displaing the simulation
     */
    public static int antsNumber = 0;
    private int tileSize;
    private int nTiles;
    private Grid grid;

    public TileCanvas(int tileSize, Grid grid) {
        this.tileSize = tileSize;
        this.nTiles = grid.getSize();
        this.grid = grid;
        // setPreferredSize(new Dimension(tileSize * nTiles, tileSize * nTiles));
        setSize(new Dimension(tileSize * nTiles, tileSize * nTiles));
        setBackground(Color.black);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        for (Point p : (HashSet<Point>) grid.getFood().clone()) {
            g.setColor(Color.green);
            g.fillRect(p.x * tileSize, p.y * tileSize, tileSize, tileSize);
        }
        for (Ant ant : (ArrayList<RedAnt>) grid.getRedAnts().clone()) {
            g.setColor(ant.getColor());
            g.fillRect(ant.getPosition().x * tileSize, ant.getPosition().y * tileSize, tileSize, tileSize);
        }
        for (Ant ant : (ArrayList<BlueAnt>) grid.getBlueAnts().clone()) {
            g.setColor(ant.getColor());
            g.fillRect(ant.getPosition().x * tileSize, ant.getPosition().y * tileSize, tileSize, tileSize);
        }
        // Frame.drawnSem.release();
        // Frame.updatingGrid.release();
    }

    public void updateCanvas() {
        repaint();
    }

    public int getOffset() {
        return tileSize * nTiles / 2;
    }

    public int getWindowSize() {
        return tileSize * nTiles;
    }
}