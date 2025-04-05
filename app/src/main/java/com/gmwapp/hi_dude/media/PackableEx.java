package com.gmwapp.hi_dude.media;

public interface PackableEx extends Packable {
    void unmarshal(ByteBuf in);
}
