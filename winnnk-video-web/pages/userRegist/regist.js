const app = getApp()

Page({
    data: {

    },

    doRegist: function(e) {
      var me = this;
      var formObject = e.detail.value;
      var username = formObject.username;
      var password = formObject.password;
      // 简单验证  TODO：验证长度和密码组合
      if (username.length == 0 || password.length == 0) {
        wx.showToast({
          title: '用户名或密码不能为空',
          icon: 'none',
          duration: 3000
        })
      } else {
        wx.showLoading({
          title: '请等待...',
        });
        var serverUrl = app.serverUrl;
        wx.request({
          url: serverUrl + '/regist',
          method: "POST",
          data: {
            username: username,
            password: password
          },
          header: {
            'content-type': 'application/json' // 默认值
          },
          success: function(res) {
            wx.hideLoading();
            var status = res.data.status;
            if (status == 200) {
              wx.showToast({
                title: "用户注册成功！",
                icon: 'none',
                duration: 3000
              }),
              //保存当前用户至全局用户信息，这个用户信息就是后端封装的UsersVO对象，
              //包含userToken、头像路径、粉丝数量、关注数量、收到的赞等等
              app.setGlobalUserInfo(res.data.data);
              wx.redirectTo({
                url: '../index/index',
              })
            }
             else if (status == 500) {
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

    goLoginPage:function() {
      wx.redirectTo({
        url: '../userLogin/login',
      })
    }
})