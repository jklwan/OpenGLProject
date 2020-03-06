attribute vec2 aPosition;
attribute vec3 aColor;
attribute vec2 aOffset;

varying vec3 fColor;

void main(){
    fColor = aColor;
    gl_Position = vec4(aPosition + aOffset, 0.0, 1.0);
}