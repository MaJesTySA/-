const app = getApp()

Page({
  data: {

  },

  onLoad: function(params) {
    var me = this;
    var redirectUrl = params.redirectUrl;
    if (redirectUrl != null || redirectUrl != undefined) {
      redirectUrl = redirectUrl.replace(/#/g, "?").replace(/@/g, "=");
      me.redirectUrl = redirectUrl;
    }
  },

  doLogin: function(e) {
    var me = this;
    var formObject = e.detail.value;
    var username = formObject.username;
    var password = formObject.password;
    // 简单验证
    if (username.length == 0 || password.length == 0) {
      wx.showToast({
        title: '用户名或密码不能为空',
        icon: 'none',
        duration: 3000
      })
    } else {
      var serverUrl = app.serverUrl;
      wx.showLoading({
        title: '请等待...',
      });
      wx.request({
        url: serverUrl + '/login',
        method: "POST",
        data: {
          username: username,
          password: password
        },
        header: {
          'content-type': 'application/json'
        },
        success: function(res) {
          wx.hideLoading();
          var status = res.data.status;
          if (status == 200) {
            wx.showToast({
                title: "登录成功！",
                icon: 'success',
                duration: 2000
              }),
              //将登陆的用户设置到全局
              app.setGlobalUserInfo(res.data.data);

            var redirectUrl = me.redirectUrl;
            if (redirectUrl != null && redirectUrl != undefined && redirectUrl != ' ') {
              wx.redirectTo({
                url: redirectUrl,
              })
            } else {
              wx.navigateTo({
                url: '../mine/mine',
              })
            }

          } else if (status == 500) {
            wx.showToast({
              title: res.data.msg,
              icon: 'none',
              duration: 3000
            })
          }
        }
      })
    }
  },

  goRegistPage: function() {
    wx.redirectTo({
      url: '../userRegist/regist',
    })
  }
})