//
//  SearchLifeCell.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 30..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import UIKit

class SearchLifeCell: UITableViewCell {
    
    // 검색 결과 화면에서 생활정보 셀 관리

    @IBOutlet weak var lifePhoto: UIImageView! // 마켓사진
    @IBOutlet weak var lifeName: UILabel! // 마켓이름
    @IBOutlet weak var callButton: UIButton! // 전화버튼

    override func awakeFromNib() {
        super.awakeFromNib()
        // Initialization code
    }

    override func setSelected(_ selected: Bool, animated: Bool) {
        super.setSelected(selected, animated: animated)

        // Configure the view for the selected state
    }

}
