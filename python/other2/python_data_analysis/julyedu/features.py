import matplotlib
import numpy as np
from scipy.ndimage import uniform_filter


def extract_features(imgs, feature_fns, verbose=False):
  """
    批量抽取特征

    输入:
    - imgs: N x H X W X C 的N张图片像素矩阵.
    - feature_fns: 特征函数list(因为可能要抽取很多不同的特征嘛)
    - verbose: 布尔型变量，如果是True，会输出一些中间信息

    输出:
    - 形状为(F_1 + ... + F_k, N)的数组，其中每一行对应的是这张图片的不同特征拼接成的向量
  """
  num_images = imgs.shape[0]
  if num_images == 0:
    return np.array([])

  # 确定特征维度
  feature_dims = []
  first_image_features = []
  for feature_fn in feature_fns:
    feats = feature_fn(imgs[0].squeeze())
    assert len(feats.shape) == 1, 'Feature functions must be one-dimensional'
    feature_dims.append(feats.size)
    first_image_features.append(feats)

  # 在第一张图片上确定维度之后，开了比较大的空间去存储这些后续图片的特征
  total_feature_dim = sum(feature_dims)
  imgs_features = np.zeros((total_feature_dim, num_images))
  imgs_features[:total_feature_dim, 0] = np.hstack(first_image_features)

  # 为剩下的图片抽取特征
  for i in xrange(1, num_images):
    idx = 0
    for feature_fn, feature_dim in zip(feature_fns, feature_dims):
      next_idx = idx + feature_dim
      imgs_features[idx:next_idx, i] = feature_fn(imgs[i].squeeze())
      idx = next_idx
    if verbose and i % 1000 == 0:
      print 'Done extracting features for %d / %d images' % (i, num_images)

  return imgs_features


def rgb2gray(rgb):
  """
  把RGB图像转成灰度图  
  """
  return np.dot(rgb[...,:3], [0.299, 0.587, 0.144])


def hog_feature(im):
  """
      计算图像的Histogram of Gradient (HOG)特征
  
      根据skimage.feature.hog改写
      http://pydoc.net/Python/scikits-image/0.4.2/skimage.feature.hog
     
      参考:
      Histograms of Oriented Gradients for Human Detection
      Navneet Dalal and Bill Triggs, CVPR 2005
     
      参数:
      im : 一张图片
      
      返回值:
      feat: Histogram of Gradient (HOG) 特征
    
  """
  
  # 转成灰度图
  if im.ndim == 3:
    image = rgb2gray(im)
  else:
    image = np.at_least_2d(im)

  sx, sy = image.shape # image size
  orientations = 9 # number of gradient bins
  cx, cy = (8, 8) # pixels per cell

  gx = np.zeros(image.shape)
  gy = np.zeros(image.shape)
  gx[:, :-1] = np.diff(image, n=1, axis=1) # compute gradient on x-direction
  gy[:-1, :] = np.diff(image, n=1, axis=0) # compute gradient on y-direction
  grad_mag = np.sqrt(gx ** 2 + gy ** 2) # gradient magnitude
  grad_ori = np.arctan2(gy, (gx + 1e-15)) * (180 / np.pi) + 90 # gradient orientation

  n_cellsx = int(np.floor(sx / cx))  # number of cells in x
  n_cellsy = int(np.floor(sy / cy))  # number of cells in y
  # compute orientations integral images
  orientation_histogram = np.zeros((n_cellsx, n_cellsy, orientations))
  for i in range(orientations):
    # create new integral image for this orientation
    # isolate orientations in this range
    temp_ori = np.where(grad_ori < 180 / orientations * (i + 1),
                        grad_ori, 0)
    temp_ori = np.where(grad_ori >= 180 / orientations * i,
                        temp_ori, 0)
    # select magnitudes for those orientations
    cond2 = temp_ori > 0
    temp_mag = np.where(cond2, grad_mag, 0)
    orientation_histogram[:,:,i] = uniform_filter(temp_mag, size=(cx, cy))[cx/2::cx, cy/2::cy].T
  
  return orientation_histogram.ravel()


def color_histogram_hsv(im, nbin=10, xmin=0, xmax=255, normalized=True):
  """
  Compute color histogram for an image using hue.

  Inputs:
  - im: H x W x C RGB像素数组
  - nbin: 分成多少个bin (default: 10)
  - xmin: 最小像素值 (default: 0)
  - xmax: 最大像素值 (default: 255)
  - normalized: 是否是归一化的 (default: True)

  Returns:
    对应bin大小的numpy list特征
  """
  ndim = im.ndim
  bins = np.linspace(xmin, xmax, nbin+1)
  hsv = matplotlib.colors.rgb_to_hsv(im/xmax) * xmax
  imhist, bin_edges = np.histogram(hsv[:,:,0], bins=bins, density=normalized)
  imhist = imhist * np.diff(bin_edges)

  # 返回 histogram
  return imhist


pass
