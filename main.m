clear
clc
vid = VideoReader('videoplayback1.mp4');
ob_det = vision.ForegroundDetector('NumGaussians', 3, 'NumTrainingFrames', 50);
for i = 1:150
frame = readFrame(vid);
ob = step(ob_det, frame);
end
figure; imshow(frame); title('Video Frame');
figure; imshow(ob); title('The Object');
Structure = strel('square', 3);
noiseless_ob = imopen(ob, Structure);
figure; imshow(noiseless_ob); title('Object After Removing Noise');
bound_b = vision.BlobAnalysis('BoundingBoxOutputPort', true, ...
    'AreaOutputPort', false, 'CentroidOutputPort', false, ...
'MinimumBlobArea', 150);
box = step(bound_b, noiseless_ob);
dc = insertShape(frame, 'Rectangle', box, 'Color', 'green');
nc = size(box, 1);
dc = insertText(dc, [10 10], nc, 'BoxOpacity', 1,'FontSize', 14);
figure; imshow(dc); title('Detected Cars');
videoPlayer = vision.VideoPlayer('Name', 'Detected Cars');
videoPlayer.Position(3:4) = [650,400];
gl=0;
while hasFrame(vid)
    frame = readFrame(vid); 
    ob = step(ob_det, frame); 
    noiseless_ob = imopen(ob, Structure); 
    box = step(bound_b, noiseless_ob); 
    dc = insertShape(frame, 'Rectangle', box, 'Color', 'green'); 
    nc = size(box, 1); 
    dc = insertText(dc, [10 10], nc, 'BoxOpacity', 1, 'FontSize', 14); 
    step(videoPlayer, dc);
end
release(videoPlayer);