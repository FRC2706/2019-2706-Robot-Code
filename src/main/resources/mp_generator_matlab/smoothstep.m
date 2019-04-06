function y = smoothstep(t, edge0, edge1)
%SMOOTHSTEP Produced smoothed step time history y for time history t h tran
% y = 0 for t <= 0, y = 1 for t >= 1, and there is a smooth transition 
% for 0 < t < 1.
t2 = clamp((t - edge0) / (edge1 - edge0), 0.0, 1.0);
y = t2 .* t2 .* (3.0 - 2.0 * t2);
end

