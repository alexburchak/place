package org.alexburchak.place.frontend;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.alexburchak.place.common.data.Constant;
import org.alexburchak.place.common.data.Pixel;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class DumpImage {
	private static final long COLORS[] = {
			0xffffff,
			0xfbf305,
			0xfd6361,
			0xdd0907,
			0xf20884,
			0x4700a5,
			0x0000d3,
			0x02abea,
			0x1fb714,
			0x006412,
			0x562c05,
			0xa6593d,
			0xdaa989,
			0xc98a5e,
			0x404040,
			0x000000
	};
	
	public static void main(String[] args) throws IOException {
	    if (args.length != 2) {
	        System.out.println("Usage:\n\tDumpImage <image input file> <dump output file>");
	        return;
        }

		BufferedImage image = ImageIO.read(new File(args[0]));

		ObjectWriter pixelWriter = new ObjectMapper()
                .writer()
                .forType(Pixel.class);

		try (FileWriter dump = new FileWriter(new File(args[1]))){
            for (int y = 0; y < image.getHeight(); y++) {
                for (int x = 0; x < image.getWidth(); x++) {
                    long rgb = ((long) image.getRGB(x, y)) & 0xffffff;

                    Pixel pixel = new Pixel(x, y, getColor(rgb));
                    int partition = pixel.getPartition(Constant.MAX_WIDTH, Constant.PARTITION_SIZE);

                    dump.write(String.valueOf(partition));
                    dump.write("/");
                    dump.write(pixelWriter.writeValueAsString(pixel));
                    dump.write("\n");
                }
            }
        }
	}

    private static byte getColor(long rgb) {
		long distance = Long.MAX_VALUE;
		byte index = 0;

		for (byte i = 0; i < COLORS.length; i++) {
			long color = COLORS[i];

			long red1 = ((rgb >> 16) & 0xFF);
			long red2 = ((color >> 16) & 0xFF);
			long rmean = ((red1 + red2) >> 1);

			long r = red1 - red2;
			long g = ((rgb >> 8) & 0xFF) - ((color >> 8) & 0xFF);
			long b = (rgb & 0xFF) - (color & 0xFF);

			long distance2 = (((512 + rmean) * r * r) >> 8) + 4 * g * g + (((767-rmean) * b * b) >> 8);
			if (distance2 < distance) {
				distance = distance2;
				index = i;
			}
		}

		return index;
	}
}
