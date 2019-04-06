function [y v] = ssp(td, s)
%ssp(td, s): Smooth step profile composed of a series of smoothed steps
%   td: time increment between each point
%   s:  nsteps x 3 matrix, where nsteps is the number of steps in the
%       profile, where the ith row gives the following: height of the step,
%       time when the step starts, and time when the step ends
[nr, nc] = size(s);
t = 0:td:s(nr,3);
y = s(1,1)*smoothstep(t, 0, s(1,2));
for i = 2:nr
    y = y + (s(i,1) - s(i-1,1))*smoothstep(t, s(i-1,3), s(i,2));
end
v = [0, diff(y)/td];
plot(t, y);
end

