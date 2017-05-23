//
//  ViewController.swift
//  Delion
//
//  Created by kwonnahyun on 2016. 10. 16..
//  Copyright © 2016년 kwonnahyun. All rights reserved.
//

import Foundation
import UIKit

class ViewController: UIViewController, UISearchBarDelegate {
    
    // 메인 아울렛
    
    @IBOutlet weak var mainSegment: UISegmentedControl! // 세그먼트(배달,생활정보)
    @IBOutlet weak var mainContainer1: UIView! // 배달 Icon View
    @IBOutlet weak var mainContainer2: UIView! // 생활정보 Icon View
    @IBOutlet weak var searchBar: UISearchBar! // 검색창
    
    
    override func viewDidLoad() {
        super.viewDidLoad()
        self.title = "딜라이온"
        self.searchBar.delegate = self
        
        // 메인 컨테이너 세팅
        mainContainer1.isHidden = false
        mainContainer2.isHidden = true
    }

    override func didReceiveMemoryWarning() {
        super.didReceiveMemoryWarning()
        // Dispose of any resources that can be recreated.
    }
    
    // 세그먼트(배달,생활정보) 클릭 시
    @IBAction func mainSegmentAction(sender: AnyObject) {
        if(mainSegment.selectedSegmentIndex == 0) // 배달
        {
            mainContainer1.isHidden = false
            mainContainer2.isHidden = true
        }
        else if(mainSegment.selectedSegmentIndex == 1) // 생활정보
        {
            mainContainer1.isHidden = true
            mainContainer2.isHidden = false
        }
    }
    
    // 검색바 클릭 시 searchSegue 활성화
    func searchBarSearchButtonClicked(_ searchBar: UISearchBar) {
        performSegue(withIdentifier: "searchSegue", sender: nil)
        self.searchBar.text = ""
    }
    
    // searchSegue 활성화 시, 검색 스트링 넘김
    override func prepare(for segue: UIStoryboardSegue, sender: Any?) {
        let searchresult = segue.destination as? SearchView
        searchresult?.searchstring = self.searchBar.text!
    }
}

