package com.malikbisic.sportapp.classes;

import com.bumptech.glide.load.ResourceDecoder;
import com.bumptech.glide.load.engine.Resource;
import com.bumptech.glide.load.resource.SimpleResource;
import com.caverock.androidsvg.SVG;
import com.caverock.androidsvg.SVGParseException;

import java.io.IOException;
import java.io.InputStream;

/**
 * Created by Nane on 7.11.2017.
 */

public class SvgDecoder implements ResourceDecoder<InputStream,SVG> {

    private SvgFileReolver svgFileResolver;

    @Override
    public Resource<SVG> decode(InputStream source, int width, int height) throws IOException {
        svgFileResolver = new SvgFileReolver();
        try {
            SVG svg = SVG.getFromInputStream(source);
            svg.registerExternalFileResolver(svgFileResolver);

            return new SimpleResource<SVG>(svg);
        } catch (SVGParseException ex) {
            throw new IOException("Cannot load SVG from stream", ex);
        }
    }

    @Override
    public String getId() {
        return "com.malikbisic.sportapp";
    }
}
