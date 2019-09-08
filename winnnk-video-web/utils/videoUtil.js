function uploadVideo() {
  var me = this;
  wx.chooseVideo({
    sourceType: ['album'],
    success: function(res) {
      //获取视频的信息
      var duration = res.duration;
      var tmpHeight = res.height;
      var tmpWidth = res.width;
      var tmpVideoUrl = res.tempFilePath;
      var tempCoverUrl = res.thumbTempFilePath;
      //超过15秒不能上传
      if (duration > 15) {
        wx.showToast({
          title: '视频长度不能超过15秒',
          icon: "none",
          duration: 2500
        })
      } else if (duration < 1) {
        wx.showToast({
          title: '视频长度太短，请上传超过1秒的视频',
          icon: "none",
          duration: 2500
        })
      } else {
        wx.navigateTo({
          //转发至chooseBgm，附带视频信息，比如临时视频连接/
          url: '../chooseBgm/chooseBgm?duration=' + duration +
            '&tmpHeight=' + tmpHeight +
            '&tmpWidth=' + tmpWidth +
            '&tmpVideoUrl=' + tmpVideoUrl +
            '&tempCoverUrl=' + tempCoverUrl,
        })
      }
    }
  })
}

module.exports={
  uploadVideo:uploadVideo
}