package jpp.infinityloop;

import jpp.infinityloop.game.board.Board;
import jpp.infinityloop.game.board.BoardSolver;
import jpp.infinityloop.game.tiles.Orientation;
import jpp.infinityloop.game.tiles.Tile;
import jpp.infinityloop.game.tiles.TileType;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.ArrayList;

public class BlobCoder {

    private final static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public static byte[] encode(Board board){

        if(board == null){
            throw new IllegalArgumentException("No board given.");
        }

        StringBuilder hexCode = new StringBuilder();

        //start with magic number
        hexCode.append("e2889e");

        // width
        ByteBuffer bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt((int) board.getWidth());

        //hexCode.append(HexBin.encode(bb.array()));
        hexCode.append(bytesToHex(bb.array()));

        // height
        bb = ByteBuffer.allocate(4);
        bb.order(ByteOrder.LITTLE_ENDIAN);
        bb.putInt((int) board.getHeight());

        //hexCode.append(HexBin.encode(bb.array()));
        hexCode.append(bytesToHex(bb.array()));

        int decimal;
        StringBuilder binaryString;
        int[] code = new int[4];
        ArrayList<Tile>[][] tiles;
        tiles = board.getTiles();

        // now the tiles
        for(int i = 0; i < board.getHeight(); i++){
            for(int j = 0; j < board.getWidth(); j++){

                binaryString = new StringBuilder();
                code = tiles[i][j].get(0).getCode();

                for(int c = 0; c < 4; c++){
                    binaryString.append(code[c]);
                }

                decimal = Integer.parseInt(binaryString.toString(),2);
                hexCode.append(Integer.toString(decimal,16));
            }
        }

        // fill up if necessary
        if((board.getWidth()*board.getHeight()) % 2 != 0){
            hexCode.append("0");
        }

        //System.out.println(hexCode.toString());
        //return javax.xml.bind.DatatypeConverter.parseHexBinary(hexCode.toString());
        String level = hexCode.toString();
        int len = level.length();
        byte[] data = new byte[len/2];
        for(int i = 0; i < len; i+= 2){
            data[i/2] = (byte) ((Character.digit(level.charAt(i), 16) << 4) + Character.digit(level.charAt(i+1), 16));
        }
        return data;
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length *2];
        for(int i = 0; i < bytes.length; i++){
            int v = bytes[i] & 0xFF;
            hexChars[i*2] = hexArray[v >>> 4];
            hexChars[i*2 +1] = hexArray[v & 0x0F];
        }

        return new String(hexChars);
    }


    public static Board decode(byte[] binaryCode){

        //String hexCode = (HexBin.encode(binaryCode));
        String hexCode = (bytesToHex(binaryCode));
        hexCode = hexCode.toLowerCase();

        // check magic number
        String magicNumber = hexCode.substring(0,6);

        if(!(magicNumber.equals("e2889e"))){
            return null;
            //throw new IllegalArgumentException("Incompatible file encoding (Wrong magicNumber)");
        }

        // get width
        String widthString = hexCode.substring(6, 14);
        int dez = Integer.parseInt(widthString,16);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asIntBuffer().put(dez);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int width = buffer.asIntBuffer().get();

        if(width < 1){
            return null;
            //throw new IllegalArgumentException("Illegal board width");
        }

        // get height
        String heightString = hexCode.substring(14,22);
        int dez1 = Integer.parseInt(heightString, 16);

        ByteBuffer buffer1 = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asIntBuffer().put(dez1);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int height = buffer.asIntBuffer().get();

        if(height < 1){
            return null;
            //throw new IllegalArgumentException("Illegal board height");
        }

        int fieldCount = width*height;

        if(hexCode.length()-22 != fieldCount){
            if(hexCode.length()-22 != fieldCount+1 ){
                return null;

            } else if(hexCode.length()-22 == fieldCount+1){
                if(!hexCode.endsWith("0")){
                    return null;
                }
            }


        }

        // get the tiles
        String tile;
        int pos = 22;
        ArrayList<Tile>[][] tiles = new ArrayList[height][width];
        for(int i = 0; i < height; i++){
            for(int j = 0; j < width; j++){
                tiles[i][j] = new ArrayList<>();

                tile = hexCode.substring(pos,pos+1);
                pos++;

                switch (tile){
                    case "0":
                        tiles[i][j].add(new Tile(TileType.EMPTY, Orientation.UP));
                        //System.out.print("\u2007");
                        break;
                    case "1":
                        tiles[i][j].add(new Tile(TileType.DEAD_END, Orientation.DOWN));
                        //System.out.print("\u257B");
                        break;
                    case "2":
                        tiles[i][j].add(new Tile(TileType.DEAD_END, Orientation.RIGHT));
                        //System.out.print("\u257A");
                        break;
                    case "3":
                        tiles[i][j].add(new Tile(TileType.BEND, Orientation.RIGHT));
                        //System.out.print("\u250f");
                        break;
                    case "4":
                        tiles[i][j].add(new Tile(TileType.DEAD_END, Orientation.UP));
                        //System.out.print("\u2579");
                        break;
                    case "5":
                        tiles[i][j].add(new Tile(TileType.STRAIGHT, Orientation.UP));
                        //System.out.print("\u2503");
                        break;
                    case "6":
                        tiles[i][j].add(new Tile(TileType.BEND, Orientation.UP));
                        //System.out.print("\u2517");
                        break;
                    case "7":
                        tiles[i][j].add(new Tile(TileType.TEE, Orientation.UP));
                        //System.out.print("\u2523");
                        break;
                    case "8":
                        tiles[i][j].add(new Tile(TileType.DEAD_END, Orientation.LEFT));
                        //System.out.print("\u2578");
                        break;
                    case "9":
                        tiles[i][j].add(new Tile(TileType.BEND, Orientation.DOWN));
                        //System.out.print("\u2513");
                        break;
                    case "a":
                        tiles[i][j].add(new Tile(TileType.STRAIGHT, Orientation.LEFT));
                        //System.out.print("\u2501");
                        break;
                    case "b":
                        tiles[i][j].add(new Tile(TileType.TEE, Orientation.RIGHT));
                        //System.out.print("\u2533");
                        break;
                    case "c":
                        tiles[i][j].add(new Tile(TileType.BEND, Orientation.LEFT));
                        //System.out.print("\u251b");
                        break;
                    case "d":
                        tiles[i][j].add(new Tile(TileType.TEE, Orientation.DOWN));
                        //System.out.print("\u252b");
                        break;
                    case "e":
                        tiles[i][j].add(new Tile(TileType.TEE, Orientation.LEFT));
                        //System.out.print("\u253b");
                        break;
                    case "f":
                        tiles[i][j].add(new Tile(TileType.CROSS, Orientation.UP));
                        //System.out.print("\u254b");
                        break;
                    default:
                        throw new IllegalStateException("Code didn't find a tile. BlobCoder.decode(...)");
                }

            }
            //System.out.println();
        }

        // final test

        return new Board(width, height, tiles);

    }



    //test
    public static void main(String[] args) {
        /*byte[] test = "e2889e".getBytes();

        for(int i = 0; i < test.length; i++){
            //System.out.println(test[i]);
        }

        byte[] bytes = javax.xml.bind.DatatypeConverter.parseHexBinary("e2889e");

        for(int i = 0; i < bytes.length; i++){
            System.out.println(bytes[i]);
        }

        byte[] code = "\u221e".getBytes();
        for(int i = 0; i < code.length; i++){
            System.out.println(code[i]);
        }

        code[0] = 0x3b;
        //System.out.println(code[0]);

        code[1] = 0b00111011;
        //System.out.println(code[1]);

        System.out.println();

        ArrayList<Tile>[][] tiles = new ArrayList[2][3];
        for(int i = 0; i < 2; i++){
            for(int j = 0; j < 3; j++){
                tiles[i][j] = new ArrayList<>();
            }
        }
        tiles[0][0].add(new Tile(TileType.BEND, Orientation.RIGHT));
        tiles[0][1].add(new Tile(TileType.TEE, Orientation.RIGHT));
        tiles[0][2].add(new Tile(TileType.BEND, Orientation.DOWN));
        tiles[1][0].add(new Tile(TileType.BEND, Orientation.UP));
        tiles[1][1].add(new Tile(TileType.TEE, Orientation.LEFT));
        tiles[1][2].add(new Tile(TileType.BEND, Orientation.LEFT));

        Board b = new Board(3,2,tiles);

        //encode(b);*/

        /*String a = "02000000";
        System.out.println(Integer.parseInt(a, 16));

        int dez = Integer.parseInt(a, 16);

        ByteBuffer buffer = ByteBuffer.allocate(4);
        buffer.order(ByteOrder.BIG_ENDIAN);
        buffer.asIntBuffer().put(dez);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        int flipped = buffer.asIntBuffer().get();
        System.out.println(flipped);*/

        String tet = "3b9";
        String code = tet.substring(2,3);
        //System.out.println(code);

        //decode(encode(b));

        /*//String hexCode = "e2889e03000000020000003b96ec";
        String hexCode ="e2889e03000000020000003b96ec";
        System.out.println(hexCode);

        byte[] binary = javax.xml.bind.DatatypeConverter.parseHexBinary(hexCode.toString());

        decode(binary);

        hexCode = (HexBin.encode(binary));

        //System.out.println(hexCode);

        hexCode = hexCode.toLowerCase();
        System.out.println(hexCode);*/

        Controller controller = new Controller();
        controller.handleShuffle();
        BoardSolver solver = new BoardSolver();

        for(int i = 0; i < 1000*10; i++) {
            solver.solve(decode(encode(controller.handleNext())));
            //decode(encode(solver.solveHelper(controller.handleNext())));
        }

    }
}

