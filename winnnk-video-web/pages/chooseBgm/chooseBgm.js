const app = getApp()
Page({
  data: {
    bgmList: [],
    serverUrl: app.serverUrl,
    videoParams: {}
  },

  onLoad: function(params) {
    var me = this;
    var serverUrl = app.serverUrl;
    var user = app.getGlobalUserInfo();
    me.setData({
      //设置从mine.js的上传视频中传过来的视频信息，
      //是URL的形式，不是json-data对象
      videoParams: params
    });

    wx.showLoading({
      title: '请等待...',
    })
    wx.request({
      url: serverUrl + '/bgm/list',
      method: "POST",
      header: {
        'content-type': 'application/json',
        'userId': user.id,
        'userToken': user.userToken
      },
      success: function(res) {
        wx.hideLoading();
        if (res.data.status == 200) {
          //bgmList是一个包含所有bgm的JSON对象
          var bgmList = res.data.data;
          me.setData({
            bgmList: bgmList,
          })
        } else if (res.data.status == 502) {
          wx.showToast({
            title: res.data.msg,
            duration: 2000,
            icon: "none",
            success: function() {
              wx.redirectTo({
                url: '../userLogin/login',
              })
            }
          });
        }
      }
    })
  },

  upload: function(e) {
    var me = this;
    //bmgId和desc是表单提交过来的数据
    var bgmId = e.detail.value.bgmId;
    var desc = e.detail.value.desc;
    //从上传视频拿出来的视频数据
    var duration = me.data.videoParams.duration;
    var tmpHeight = me.data.videoParams.tmpHeight;
    var tmpWidth = me.data.videoParams.tmpWidth;
    var tmpVideoUrl = me.data.videoParams.tmpVideoUrl;
    var tempCoverUrl = me.data.videoParams.tempCoverUrl;
    //上传用户id
    var userInfo = app.getGlobalUserInfo();
    wx.showLoading({
      title: '上传中',
    })
    wx.uploadFile({
      url: app.serverUrl + '/video/upload',
      formData: {
        userId: userInfo.id,
        bgmId: bgmId,
        desc: desc,
        videoSeconds: duration,
        videoWidth: tmpWidth,
        videoHeight: tmpHeight
      },
      filePath: tmpVideoUrl,
      name: 'file',
      header: {
        'content-type': 'application/json',
        'userId': userInfo.id,
        'userToken': userInfo.userToken
      },

      success: function(res) {
        wx.hideLoading();
        var data = JSON.parse(res.data);
        if (data.status == 200) {
          wx.showToast({
            title: '上传成功！',
            icon: "success"
          })
          wx.navigateBack({
            delta: 1
          })
        }
      }
    })
  }
})