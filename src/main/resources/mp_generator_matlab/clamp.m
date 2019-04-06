function y = clamp(x,bl,bu)
% clamp(y,bl,bu): Restricts sequence of points x to be in the interval
% [bl,bu]
  y=min(max(x,bl),bu);
end

