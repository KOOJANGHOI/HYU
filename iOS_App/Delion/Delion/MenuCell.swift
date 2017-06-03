//
//  MenuCell.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 20..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import UIKit

class MenuCell: UITableViewCell {
    
    // 배달 디테일 화면의 메뉴 종류 셀 부분
    
    @IBOutlet weak var menuName: UILabel! // 메뉴이름
    @IBOutlet weak var menuPrice: UILabel! // 메뉴가격
    
    override func awakeFromNib() {
        super.awakeFromNib()
        self.layer.borderWidth = 1
        self.layer.borderColor = UIColor(red: 245/255, green: 122/255, blue: 98/255, alpha: 1.0).cgColor
        self.menuName.adjustsFontSizeToFitWidth = true
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
