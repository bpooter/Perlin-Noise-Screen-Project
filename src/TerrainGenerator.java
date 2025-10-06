
public class TerrainGenerator {

    // noise objects
    private final FastNoiseLite baseNoise;
    private final FastNoiseLite regionNoise;
    private final FastNoiseLite warpX;
    private final FastNoiseLite warpZ;

    //Terrain configuration
    private double noiseScale = 0.005;
    private double heightScale = 120.0;
    private double warpAmount = 40.0;
    private double scrollOffset = 0.0;
    private double scrollSpeed = 0.5;

    public TerrainGenerator(){

        // base noise for height
        baseNoise = new FastNoiseLite();
        baseNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        baseNoise.SetFractalType(FastNoiseLite.FractalType.FBm);
        baseNoise.SetFractalGain(0.5f);
        baseNoise.SetFractalLacunarity(2.0f);
        baseNoise.SetFractalOctaves(5);

        //region noise controls plains hills and mountains
        regionNoise = new FastNoiseLite();
        regionNoise.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        regionNoise.SetFrequency(0.0008f);

        //warping noises
        warpX = new FastNoiseLite();
        warpX.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        warpX.SetFrequency(0.002f);

        warpZ = new FastNoiseLite();
        warpZ.SetNoiseType(FastNoiseLite.NoiseType.OpenSimplex2);
        warpZ.SetFrequency(0.002f);
    }

    public double getTerrainHeight(double x, double z){

        //apply scrolling to simulate foward motion
        double scrolledZ = z + scrollOffset;

        //domain warping
        float warpedX = (float)(x * noiseScale + warpX.GetNoise((float)x, (float) scrolledZ) * warpAmount);
        float warpedZ = (float)(scrolledZ * noiseScale + warpZ.GetNoise((float)x,(float)scrolledZ) * warpAmount);

        // region mask
        double region = regionNoise.GetNoise((float)x * 0.001f, (float)scrolledZ * 0.001f);

        double frequency;
        double amplitude;
        int octaves;

        if (region < -0.3){
            // plains
            frequency = 0.002f;
            amplitude = 0.3f;
            octaves = 3;
        } else if (region < 0.3) {
            //hills
            frequency = 0.004f;
            amplitude = 0.6f;
            octaves = 4;
        } else {
            // mountains
            frequency = 0.007f;
            amplitude = 1.0f;
            octaves = 5;
        }
        baseNoise.SetFrequency((float) frequency);
        baseNoise.SetFractalOctaves(octaves);


        double height = baseNoise.GetNoise(warpedX, warpedZ);
        return height * heightScale * amplitude;
    }

    public void updateScroll(){
        scrollOffset += scrollSpeed;
    }

    // call update scroll before this in animation loop.
    public void updateMeshHeights(double[][] heights, int width, int depth){
        for (int x = 0; x < width; x++){
            for (int z = 0; z < depth; z++){
                heights[x][z] = getTerrainHeight(x, z);
            }
        }
    }

    public void setScrollSpeed(double speed){this.scrollSpeed = speed;}
    public void setNoiseScale(double scale){this.noiseScale = scale;}
    public void setHeightScale(double scale){this.heightScale = scale;}
}
