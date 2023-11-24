import { instantiate } from './peopleinspace.uninstantiated.mjs';

await wasmSetup;

let te = null;
try {
    await instantiate({ skia: Module['asm'] });
} catch (e) {
  te = e;  
}
